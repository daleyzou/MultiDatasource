package com.daleyzou.multidatasource.dao.second;

import com.daleyzou.multidatasource.po.NodePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeSecondDao extends JpaRepository<NodePo, Integer> {

}
