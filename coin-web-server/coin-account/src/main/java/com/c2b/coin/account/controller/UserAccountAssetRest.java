package com.c2b.coin.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.service.IUserAccountAssetService;
import com.c2b.coin.web.common.BaseRest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(description="用户资产相关接口")
@RestController
@RequestMapping("/asset")
public class UserAccountAssetRest extends BaseRest{

	@Autowired
	private IUserAccountAssetService userAccountAssetService;
	
	@PostMapping("/total")
	@ApiOperation(value="获取资产总计接口",notes="'totalBTC':'总资产折算BTC','totalUSD':'总资产折算usd','availableBTC':'可用资产折算BTC','availableUSD':'可用资产折算usd','freezingBTC','冻结资产折算BTC','freezingUSD','冻结资产折算usd'")
	public String getAssetTotal() {
		long userId=Long.valueOf(this.getUserId());
		return writeJson(this.userAccountAssetService.assetTotal(userId));
	}
	
	
	@PostMapping
	@ApiOperation(value="获取总资产接口",notes="'currencyType':'货币类型标识','currencyName','货币名称','currencyFullName':'货币全称','totalAmount':'货币总量','availableAmount':'可用数量','freezingAmount':'冻结数量'")
	public String totalAsset() {
		long userId=Long.valueOf(this.getUserId());
		return writeJson(this.userAccountAssetService.getTotalAsset(userId));
	}
	
	@PostMapping("/digitalCoin")
	@ApiOperation(value="获取币种接口",notes="'coinName':'货币名称','coinFullName':'货币全称,'isEnable':'是否可用','createTime':'创建时间','updateTime':'更新时间','remark':'备注'")
	public String digitalCoin() {
		return writeJson(this.userAccountAssetService.getDigitalCoin());
	}
	@PostMapping("/nologin/digitalCoin")
	@ApiOperation(value="获取币种接口",notes="'coinName':'货币名称','coinFullName':'货币全称,'isEnable':'是否可用','createTime':'创建时间','updateTime':'更新时间','remark':'备注'")
	public String noLogindigitalCoin() {
		return writeJson(this.userAccountAssetService.getDigitalCoin());
	}
	
	
	@PostMapping("/history")
	@ApiOperation(value="资产历史接口",notes="'userId':'用户id','username':'用户名','orderNo':'平台订单号','externalOrderNo':'交易hx','createtime':'操作时间','currencyType':'操作币种id','currencyName':'操作币种名称','asset':'操作数量','poundage':'手续费','actualAsset':'实际到账','afterAsset':'操作后数量','address':'地址','result':'操作结果1:待确认 2:已确认 3:已成功','remark':'备注'")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNo", value = "第几页，默认是1", required = false, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "pageSize", value = "分页大小,默认是20", required = false, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "type", value = "历史类型 0:全部 1:冲币 2:提币", required = false, dataType = "int", paramType = "query")
	})
	public String assetHistory(@RequestParam("pageNo") Integer pageNo,@RequestParam("pageSize") Integer pageSize,@RequestParam("type") Integer type) {
		long userId=Long.valueOf(this.getUserId());
		if(type==0) {
			type=null;
		}
		Page page=PageHelper.startPage(pageNo, pageSize, true, false);
		List<AssetLog> list=this.userAccountAssetService.getAssetHistory(type,userId);
		PageInfo<AssetLog> pageInfo=new PageInfo<>(list);
		return writeJson(pageInfo);
	}
	
	
	@PostMapping("/exchangeRate")
	@ApiOperation(value="基础货币汇率接口",notes="需要货币汇率的时候调用")
	public String exchangeRate() {
		
		return writeJson(this.userAccountAssetService.exchangeRate());
	}
	
	@PostMapping("/poundage")
	@ApiOperation(value="获取手续费接口",notes="获取手续费接口")
	@ApiImplicitParam(name = "currencyName", value = "货币名称", required = true, dataType = "string", paramType = "query")
	public String poundage(@RequestParam("currencyName")String currencyName) {
		return writeJson(this.userAccountAssetService.poundage(currencyName));
	}
	
	@PostMapping("/USDCNYRate")
	@ApiOperation(value="人民币与美元汇率",notes="获取人民币与美元的汇率")
	public String usdToCNYRate() {
		return writeJson(this.userAccountAssetService.usdToCNYRate());
	}
	
}
