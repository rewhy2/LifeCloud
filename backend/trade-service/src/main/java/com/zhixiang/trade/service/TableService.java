package com.zhixiang.trade.service;

import com.zhixiang.trade.entity.TableInfo;
import com.zhixiang.trade.mapper.TableInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {

    private final TableInfoMapper tableInfoMapper;

    public TableService(TableInfoMapper tableInfoMapper) {
        this.tableInfoMapper = tableInfoMapper;
    }

    public List<TableInfo> list(String status, String area) {
        return tableInfoMapper.selectAll(status, area);
    }

    public void save(TableInfo t) {
        if (t.getId() == null) {
            tableInfoMapper.insert(t);
        } else {
            tableInfoMapper.update(t);
        }
    }

    public void changeStatus(String no, String status) {
        tableInfoMapper.updateStatus(no, status);
    }

    public void delete(Long id) {
        tableInfoMapper.delete(id);
    }
}
