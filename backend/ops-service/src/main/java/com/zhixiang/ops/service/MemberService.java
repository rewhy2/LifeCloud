package com.zhixiang.ops.service;

import com.zhixiang.ops.entity.Member;
import com.zhixiang.ops.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MemberService {

    private final MemberMapper memberMapper;

    public MemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public List<Member> list(String name, String level) {
        return memberMapper.selectAll(name, level);
    }

    public void save(Member m) {
        if (m.getId() == null) {
            m.setLevel(m.getLevel() == null ? "NORMAL" : m.getLevel());
            m.setPoint(m.getPoint() == null ? 0 : m.getPoint());
            m.setBalance(m.getBalance() == null ? BigDecimal.ZERO : m.getBalance());
            m.setTotalSpend(m.getTotalSpend() == null ? BigDecimal.ZERO : m.getTotalSpend());
            memberMapper.insert(m);
        } else {
            memberMapper.update(m);
        }
    }

    @Transactional
    public void consume(Long id, BigDecimal amount) {
        Member m = memberMapper.selectById(id);
        if (m == null) throw new IllegalArgumentException("会员不存在");
        BigDecimal total = (m.getTotalSpend() == null ? BigDecimal.ZERO : m.getTotalSpend()).add(amount);
        int point = (m.getPoint() == null ? 0 : m.getPoint()) + amount.intValue();
        String level = total.compareTo(new BigDecimal("10000")) >= 0 ? "GOLD"
                : total.compareTo(new BigDecimal("3000")) >= 0 ? "SILVER" : "NORMAL";
        m.setTotalSpend(total);
        m.setPoint(point);
        m.setLevel(level);
        memberMapper.update(m);
    }

    public void delete(Long id) {
        memberMapper.delete(id);
    }
}
