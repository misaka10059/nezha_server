package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Created by Rexxar.Zh on 19/11/8
 */
public interface DocumentDao extends JpaRepository<Document, String> {

}
