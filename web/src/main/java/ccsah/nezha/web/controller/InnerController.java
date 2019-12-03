package ccsah.nezha.web.controller;

import ccsah.nezha.web.domain.knowledge.RoleTypes;
import ccsah.nezha.web.service.UserService;
import ccsfr.core.web.BaseApiController;
import ccsfr.core.web.ResponseData;
import com.wordnik.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Rexxar.Zh on 19/10/31
 */
@Api(value = "/inner", description = "内部模块")
@RequestMapping("inner")
@RestController
public class InnerController extends BaseApiController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/addrole", method = RequestMethod.POST)
    public ResponseData<String> addRole(@RequestParam("name") RoleTypes name) {
        userService.addRole(name);
        return ResponseData.ok("ok");
    }

    @RequestMapping(value = "/addDepartment", method = RequestMethod.POST)
    public ResponseData<String> addDepartment(@RequestParam("name") String name,
                                              @RequestParam("code") String code,
                                              @RequestParam("order") int order) {
        userService.addDepartment(name,code,order);
        return ResponseData.ok("ok");
    }


}
