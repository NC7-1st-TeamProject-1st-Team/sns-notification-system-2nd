package bitcamp.myapp.service;

import bitcamp.myapp.dao.BoardCommentDao;
import bitcamp.myapp.dao.BoardDao;
import bitcamp.myapp.vo.Board;
import bitcamp.myapp.vo.BoardComment;
import bitcamp.myapp.vo.BoardPhoto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultBoardService implements BoardService {
    {
        System.out.println("DefaultBoardService 생성됨!");
    }

    BoardDao boardDao;
    BoardCommentDao boardCommentDao;

    public DefaultBoardService(BoardDao boardDao, BoardCommentDao boardCommentDao) {
        this.boardDao = boardDao;
        this.boardCommentDao = boardCommentDao;
    }

    @Transactional // 이 메서드는 트랜잭션 상태에서 실행하라고 지정
    @Override
    public int add(Board board) throws Exception {
        int count = boardDao.insert(board);
        if (board.getAttachedFiles().size() > 0) {
            boardDao.insertFiles(board);
        }
        return count;
    }

    @Override
    public List<Board> list(int category) throws Exception {
        return boardDao.findAll(category);
    }

    @Override
    public Board get(int boardNo) throws Exception {
        return boardDao.findBy(boardNo);
    }

    @Transactional
    @Override
    public int update(Board board) throws Exception {
        int count = boardDao.update(board);
        if (count > 0 && !board.getAttachedFiles().isEmpty()) {
            boardDao.insertFiles(board);
        }
        return count;
    }

    @Transactional
    @Override
    public int delete(int boardNo) throws Exception {
        boardDao.deleteFiles(boardNo);
        boardCommentDao.deleteComments(boardNo);
        boardDao.deleteLikes(boardNo);
        return boardDao.delete(boardNo);
    }

    @Transactional
    @Override
    public int increaseViewCount(int boardNo) throws Exception {
        return boardDao.updateCount(boardNo);
    }

    @Override
    public BoardPhoto getAttachedFile(int fileNo) throws Exception {
        return boardDao.findPhotoBy(fileNo);
    }

    @Override
    public int deleteAttachedFile(int fileNo) throws Exception {
        return boardDao.deleteFile(fileNo);
    }

    // like 테이블 정보 삽입 삭제
    @Override
    public int like(int memberNo, int boardNo) throws Exception {
        return boardDao.insertLike(memberNo, boardNo);
    }

    @Override
    public int unlike(int memberNo, int boardNo) throws Exception {
        return boardDao.deleteLike(memberNo, boardNo);
    }



    // board 테이블 좋아요 수 +1 -1
    @Transactional
    @Override
    public int increaseLikes(int boardNo) throws Exception {
        return boardDao.updateLike(boardNo);
    }

    @Override
    public int decreaseLikes(int boardNo) throws Exception {
        return boardDao.cancelLike(boardNo);
    }

    @Override
    public List<Integer> likelist(int memberNo) throws Exception {
        return boardDao.findLikeByMno(memberNo);
    }
}