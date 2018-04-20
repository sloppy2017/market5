package com.c2b.ethWallet.mapper;

import java.util.List;

import com.c2b.ethWallet.entity.IcoGatherRecord;
import com.coin.config.mybatis.BaseMapper;

/**
 * 
 * @ClassName: IcoGatherRecordMapper
 * @Description: TODO(归集记录Mapper)
 * @author dxm
 * @date 2017年8月21日 下午11:25:53
 *
 */
public interface IcoGatherRecordMapper extends BaseMapper<IcoGatherRecord> {
  List<IcoGatherRecord> listGatherRecord();

  int updateGatherStatusRecordByID(IcoGatherRecord record);

  IcoGatherRecord getGetherRecordByOrderNO(String orderNO);

  int delGatherRecordByID(IcoGatherRecord record);
  
  List<IcoGatherRecord> listTOKENGatherRecord(String currency);
}