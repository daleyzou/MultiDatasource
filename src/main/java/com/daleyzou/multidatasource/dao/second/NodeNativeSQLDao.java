package com.daleyzou.multidatasource.dao.second;

import com.daleyzou.multidatasource.po.NodePo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @ClassName NodeNativeSQLDao
 * @Description: 在dao层使用第二数据源， 用EntityManager， 需要指定对应的 unitName
 * @Author dalelyzou
 * @Date 2020/10/7
 * @Version V1.0
 **/
@Repository
@Slf4j
public class NodeNativeSQLDao {

    @PersistenceContext(unitName = "secondPersistenceUnit")
    private EntityManager entityManager;

    /**
     *  使用自定义SQL查询数据
     *
     * @param
     * @return
     * @author daleyzou
     */
    public List<NodePo> getAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * from node");
        SQLQuery sqlQuery = entityManager.createNativeQuery(sb.toString()).unwrap(SQLQuery.class);
        org.hibernate.Query query = sqlQuery.setResultTransformer(Transformers.aliasToBean(NodePo.class));
        return query.list();
    }
}
