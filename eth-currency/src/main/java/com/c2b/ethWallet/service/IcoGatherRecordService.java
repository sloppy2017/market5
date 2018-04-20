package com.c2b.ethWallet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c2b.ethWallet.entity.IcoGatherRecord;
import com.c2b.ethWallet.mapper.IcoGatherRecordMapper;

/**
 * 
* @ClassName: IcoGatherRecordService 
* @Description: TODO(归集记录service) 
* @author dxm 
* @date 2017年8月21日 下午11:16:51 
*
 */
@Service
public class IcoGatherRecordService {

	@Autowired
	private IcoGatherRecordMapper  gatherRecordMapper;
	
	public int insert(IcoGatherRecord record){
		return gatherRecordMapper.insert(record);
	}
	
	/**
	 * 
	* @Title: listGatherRecordByGather 
	* @Description: TODO(查询所有的归集记录)
	* @return List<IcoGatherRecord>    返回归集记录列表 
	* @throws 
	* @author Anne
	 */
	public List<IcoGatherRecord> listGatherRecordByGather(){
		return gatherRecordMapper.listGatherRecord();
	}
	
	/**
	 * 
	* @Title: updateGetherRecord 
	* @Description: TODO(更新归集记录数据) 
	* @param record 归集记录
	* @return int    返回更新记录条数
	* @throws 
	* @author Anne
	 */
	public int updateGetherRecord(IcoGatherRecord record){
		return gatherRecordMapper.updateGatherStatusRecordByID(record);
	}
	
	/**
	 * 
	* @Title: getIcoGetherRecordByOrderId 
	* @Description: TODO(根据订单号获取归集记录) 
	* @param orderNo 订单号
	* @return IcoGatherRecord    返回归集记录数据
	* @throws 
	* @author Anne
	 */
	public IcoGatherRecord getIcoGetherRecordByOrderId(String orderNo){
		return gatherRecordMapper.getGetherRecordByOrderNO(orderNo);
	}
	
	/**
	 * 
	* @Title: delGatherRecord 
	* @Description: TODO(删除归集记录) 
	* @param record 归集记录
	* @return int    返回删除条数
	* @throws 
	* @author Anne
	 */
	public int delGatherRecord(IcoGatherRecord record){
		return gatherRecordMapper.delGatherRecordByID(record);
	}
	
	/**
     * 
    * @Title: listGatherRecordByGather 
    * @Description: TODO(根据token名称查询所有待归集的TOKEN记录)
    * @return List<IcoGatherRecord>    返回待归集的TOKEN记录列表 
    * @throws 
    * @author Anne
     */
    public List<IcoGatherRecord> listTOKENGatherRecord(String currency){
        return gatherRecordMapper.listTOKENGatherRecord(currency);
    }
}
