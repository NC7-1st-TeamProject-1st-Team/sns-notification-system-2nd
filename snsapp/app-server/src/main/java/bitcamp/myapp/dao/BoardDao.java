package bitcamp.myapp.dao;

import bitcamp.myapp.vo.Board;
import bitcamp.myapp.vo.BoardPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardDao {
  int insert(Board board);
  List<Board> findAll(int category);
  Board findBy(int no);
  int update(Board board);
  int updateCount(int no);
  int delete(int no);

  int insertFiles(Board board);
  BoardPhoto findPhotoBy(int no);
  int deleteFile(int fileNo);
  int deleteFiles(int boardNo);

  int updateLike(int boardNo); // 좋아요 수 1 증가
  int cancelLike(int boardNo); // 좋아요 수 1 감소
  int insertLike(@Param("memberNo") int memberNo, @Param("boardNo") int boardNo); // 좋아요 테이블에 추가
  int deleteLike(@Param("memberNo") int memberNo, @Param("boardNo") int boardNo); // 좋아요 테이블에 삭제
  int deleteLikes(int boardNo); // 게시판 삭제시 좋아요도 삭제

  List<Integer> findLikeByMno(int memberNo);
}