package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.entity.Member;
import com.zhixiang.ops.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public Result<List<Member>> list(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String level) {
        return Result.success(memberService.list(name, level));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Member m) {
        memberService.save(m);
        return Result.success();
    }

    @PostMapping("/{id}/consume")
    public Result<Void> consume(@PathVariable Long id, @RequestParam BigDecimal amount) {
        memberService.consume(id, amount);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return Result.success();
    }
}
