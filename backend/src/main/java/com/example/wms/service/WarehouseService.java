package com.example.wms.service;

import com.example.wms.common.BusinessException;
import com.example.wms.common.PageResult;
import com.example.wms.domain.Warehouse;
import com.example.wms.dto.WarehouseSaveRequest;
import com.example.wms.dto.WarehouseStatsVO;
import com.example.wms.repository.WarehouseRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public PageResult<Warehouse> page(int page, int size, String keyword, Integer status) {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "id"));

        Specification<Warehouse> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                String like = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("code"), like),
                        cb.like(root.get("name"), like),
                        cb.like(root.get("manager"), like),
                        cb.like(root.get("address"), like)));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Warehouse> result = warehouseRepository.findAll(specification, pageRequest);
        return PageResult.of(result, w -> w);
    }

    @Transactional(readOnly = true)
    public WarehouseStatsVO stats() {
        List<Warehouse> all = warehouseRepository.findAll();
        long total = all.size();
        long enabled = all.stream().filter(w -> w.getStatus() == 1).count();
        long totalCapacity = all.stream().mapToLong(Warehouse::getCapacity).sum();
        long usedCapacity = all.stream().mapToLong(Warehouse::getUsedCapacity).sum();
        double usageRate = totalCapacity == 0 ? 0 : Math.round(usedCapacity * 10000.0 / totalCapacity) / 100.0;
        return new WarehouseStatsVO(total, enabled, total - enabled, totalCapacity, usedCapacity, usageRate);
    }

    @Transactional(readOnly = true)
    public Warehouse get(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("仓库不存在"));
    }

    @Transactional
    public Warehouse create(WarehouseSaveRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new BusinessException("仓库编码已存在");
        }
        Warehouse warehouse = new Warehouse();
        apply(warehouse, request, true);
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public Warehouse update(Long id, WarehouseSaveRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        if (!warehouse.getCode().equals(request.getCode()) && warehouseRepository.existsByCode(request.getCode())) {
            throw new BusinessException("仓库编码已存在");
        }
        apply(warehouse, request, false);
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        warehouseRepository.delete(warehouse);
    }

    @Transactional
    public Warehouse changeStatus(Long id, Integer status) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        warehouse.setStatus(status);
        return warehouseRepository.save(warehouse);
    }

    private void apply(Warehouse warehouse, WarehouseSaveRequest request, boolean isCreate) {
        int used = request.getUsedCapacity() == null ? 0 : request.getUsedCapacity();
        if (used > request.getCapacity()) {
            throw new BusinessException("已用容量不能大于总容量");
        }
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setManager(request.getManager());
        warehouse.setPhone(request.getPhone());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setUsedCapacity(used);
        warehouse.setRemark(request.getRemark());
        if (isCreate || request.getStatus() != null) {
            warehouse.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        }
    }
}
