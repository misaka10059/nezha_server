package ccsah.nezha.web.controller;

import ccsah.nezha.web.domain.dto.test.TestInfoDto;
import ccsah.nezha.web.domain.dto.test.TestUserDto;
import ccsfr.core.web.BaseApiController;
import ccsfr.core.web.ResponseData;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 *  Created by Rexxar.Zh on 19/10/31
 */
@Api(value = "/test", description = "测试")
@RequestMapping("test")
@RestController
public class TestController extends BaseApiController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ApiOperation(value = "hello", notes = "测试接口")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping(value = "/testResponse", method = RequestMethod.POST)
    @ApiOperation(value = "testResponse", notes = "测试ResponseData")
    public ResponseData<TestUserDto> testResponse(
            @RequestParam("user") @ApiParam("查询用户") String user,
            @RequestParam("id") @ApiParam("用户id") String id) {
        return ResponseData.ok(new TestUserDto(user, id));
    }

    @RequestMapping(value = "/testJson", method = RequestMethod.POST)
    @ApiOperation(value = "testJson", notes = "测试传入json")
    public ResponseData<TestUserDto> testResponse(
            @RequestParam("userInfo") @ApiParam("用户信息json") TestInfoDto testInfo) {
        return ResponseData.ok(new TestUserDto(testInfo.getUser(), testInfo.getId()));
    }


}
