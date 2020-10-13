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
 * @Description: 测试使用主数据源，默认的，有默认的主事务管理器，不用专门设置
 * @Author daleyzou
 * @Date 2020/10/9
 * @Version V1.0
 **/
@Service
public class PrimaryDataSourceTestService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NodeSecondDao nodeSecondDao;

    public List<NodePo> test() {
        return nodeSecondDao.findAll();
    }
}
