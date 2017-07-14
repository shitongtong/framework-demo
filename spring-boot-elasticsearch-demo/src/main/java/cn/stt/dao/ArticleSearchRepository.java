package cn.stt.dao;

import cn.stt.po.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/14.
 */
//泛型的参数分别是实体类型和主键类型
public interface ArticleSearchRepository extends ElasticsearchRepository<Article, Long> {
}
