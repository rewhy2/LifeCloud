package com.zhixiang.trade.service;

import com.zhixiang.trade.entity.Product;
import com.zhixiang.trade.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> list(String name, Long categoryId, Integer status) {
        return productMapper.selectAll(name, categoryId, status);
    }

    public Product get(Long id) {
        return productMapper.selectById(id);
    }

    public void save(Product product) {
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.update(product);
        }
    }

    public void updateStatus(Long id, Integer status) {
        productMapper.updateStatus(id, status);
    }

    public void delete(Long id) {
        productMapper.updateStatus(id, 0);
    }
}
