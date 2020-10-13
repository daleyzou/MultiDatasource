package com.daleyzou.multidatasource.service;

import com.daleyzou.multidatasource.dao.second.NodeSecondDao;
import com.daleyzou.multidatasource.po.NodePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName SecondDataSourceTestService
 * @Description: 测试使用第二数据源， 在使用非主数据源时，一定要显式的指定对应使用管理器，不然连接池会耗尽的
 * @Author daleyzou
 * @Date 2020/10/9
 * @Version V1.0
 **/
@Service
@Transactional(transactionManager = "transactionManagerSecond", rollbackFor = Exception.class)
public class SecondDataSourceTestService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NodeSecondDao nodeSecondDao;

    public List<NodePo> test() {
        return nodeSecondDao.findAll();
    }
}
