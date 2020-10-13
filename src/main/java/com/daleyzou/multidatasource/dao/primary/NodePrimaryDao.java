package com.daleyzou.multidatasource.dao.primary;

import com.daleyzou.multidatasource.po.NodePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodePrimaryDao extends JpaRepository<NodePo, Integer> {

}
