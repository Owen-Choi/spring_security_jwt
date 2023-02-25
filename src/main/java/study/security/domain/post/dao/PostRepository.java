package study.security.domain.post.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.security.domain.post.model.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT p.post_id AS postId, p.title, p.thumbnail_img_path AS thumbnail, t.trade_status AS tradeStatus, c.name, count(sp.scrap_id) AS likes " +
            "FROM post p "+
            "LEFT JOIN scrap_post sp on p.post_id = sp.post_id "+
            "JOIN trade t on p.post_id = t.post_id "+
            "JOIN category c on c.category_id = p.wish_category_id "+
            "WHERE p.member_id = :userId " +
            "GROUP BY p.post_id",countQuery = "SELECT count(*) FROM post p WHERE p.member_id = :userId ", nativeQuery = true)
    Page<GetUserPostInterface> getUserPostById(@Param(value = "userId") Long userId, Pageable pageable);

    interface GetUserPostInterface {
        Long getPostId();
        String getTitle();
        String getThumbnail();
        String getTradeStatus();
        String getName();
        Long getLikes();
    }
}
