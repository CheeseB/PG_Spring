package com.controller.board;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.dto.BoardDTO;
import com.dto.LikeDTO;
import com.dto.MemberDTO;
import com.service.BoardService;
import com.service.LikeService;

@RestController
@RequestMapping("/board")
public class BoardRestController {
	@Autowired
	BoardService bService;
	@Autowired
	LikeService lService;
	
	@GetMapping("/boardList")
	public RedirectView boardList(HttpServletResponse response) {
		return new RedirectView("Board/boardList");
	}
	
	@GetMapping("/gameInfoCategory/{gameInfoCategory}")
	public List<BoardDTO> gameInfoMainList(@PathVariable String gameInfoCategory) {
		List<BoardDTO> boardList = null;
		if (gameInfoCategory == null || gameInfoCategory.contentEquals("recommend")) {
			boardList = bService.recommendInfoBoardSelect();
		} else {
			boardList = bService.hitInfoBoardSelect();
		}
		return boardList;
	}
	
	@GetMapping("/qnaCategory/{qnaCategory}")
	public List<BoardDTO> qnaMainList(@PathVariable String qnaCategory){
		List<BoardDTO> boardList = null;
		if (qnaCategory == null || qnaCategory.contentEquals("recommend")) {
			boardList = bService.recommendQnaBoardSelect();
		} else {
			boardList = bService.hitQnaBoardSelect();
		}
		return boardList;
	}
	
	@GetMapping("/read/{boardId}")
	public BoardDTO boardRead(@PathVariable int boardId) {
		BoardDTO board = bService.boardRead(boardId);
		System.out.println(board);
		return board;
	}
	
	@PostMapping("/insert")
	public int boardInsert(@RequestBody BoardDTO board) {
		System.out.println(board);
		int boardId = bService.getBoardId();
		board.setBoardId(boardId);
		int result = bService.boardInsert(board);
		if (result == 1)
			return boardId;
		else
			return -1;
	}
	
	@PatchMapping("/update")
	public int boardUpdate(@RequestBody BoardDTO board) {
		System.out.println(board);
		int result = bService.boardUpdate(board);
		if (result == 0)
			return -1;
		return result;
	}
	
	@DeleteMapping("/delete/{boardId}")
	public int boardDelete(@PathVariable int boardId) {
		int result = bService.boardDelete(boardId);
		if (result == 0) 
			return -1;
		return result;
	}
	
	@GetMapping("/boardList/{boardCategory}")
	public List<BoardDTO> boardListSelect(@PathVariable String boardCategory) {
		List<BoardDTO> boardList = bService.boardListSelect(boardCategory);
		return boardList;
	}
	
	@GetMapping("/search/{boardCategory}/{searchWord}/{searchCategory}")
	public List<BoardDTO> boardSearchList(@PathVariable HashMap<String, String> searchMap) {
		List<BoardDTO> boardList = bService.boardSearchList(searchMap);
		return boardList;
	}
	
	@PatchMapping("/like/{boardLike}/{boardId}")
	public int boardLike(@PathVariable int boardLike, @PathVariable int boardId,
							 HttpSession session) {
		System.out.println("현재 좋아요 개수 : " + boardLike);
		MemberDTO login = (MemberDTO)session.getAttribute("login");
		LikeDTO like = new LikeDTO(0, login.getMbrId(), boardId, 0, 0);
		boolean isComplete = false;
		int cnt = lService.likeBoardCount(like);
		if (cnt >= 1) {
			boardLike += bService.boardLikeMinus(boardId) * -1;
			isComplete = lService.likeBoardDelete(like);
		} else {
			boardLike += bService.boardLikePlus(boardId);
			isComplete = lService.likeBoardInsert(like);
		}
		System.out.println("좋아요 : " + boardLike + " , boardLiked 개수 : " + cnt + " , 삭제, 삽입 : " + isComplete);
		return boardLike;
	}
}