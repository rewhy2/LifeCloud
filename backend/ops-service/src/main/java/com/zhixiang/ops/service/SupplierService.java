package com.zhixiang.ops.service;

import com.zhixiang.ops.entity.Supplier;
import com.zhixiang.ops.mapper.SupplierMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierMapper supplierMapper;

    public SupplierService(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    public List<Supplier> list() {
        return supplierMapper.selectAll();
    }

    public void save(Supplier s) {
        if (s.getId() == null) {
            supplierMapper.insert(s);
        } else {
            supplierMapper.update(s);
        }
    }

    public void delete(Long id) {
        supplierMapper.delete(id);
    }
}
