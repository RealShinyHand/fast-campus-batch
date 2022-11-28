package com.fastcampus.hellospringbatch.core.repository;

import com.fastcampus.hellospringbatch.core.domain.PlainText;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlainTextRepository extends JpaRepository<PlainText,Integer> {

    //@Query("SELECT p FROM PlainText")
    //위와 같이 쿼리 작성해도 pageable 넘겨주면,알아서 해줌
    Page<PlainText> findBy(Pageable pageable);
}
