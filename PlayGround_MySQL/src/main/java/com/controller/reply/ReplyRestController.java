package com.controller.reply;

import java.util.HashMap;
import java.util.List;

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

import com.dto.LikeDTO;
import com.dto.MemberDTO;
import com.dto.ReplyDTO;
import com.dto.ReplyRestDTO;
import com.service.LikeService;
import com.service.ReplyService;

@RestController
@RequestMapping("/reply")
public class ReplyRestController {
	@Autowired
	ReplyService rService;
	@Autowired
	LikeService lService;

	@GetMapping("/read/{boardId}")
	public List<ReplyRestDTO> replyRead(@PathVariable int boardId, HttpSession session) {
		MemberDTO login = (MemberDTO) session.getAttribute("login");
		List<ReplyDTO> replyList = rService.replyRead(boardId);
		List<ReplyRestDTO> replyRestList = null;
		System.out.println(replyList);
		if (replyList != null)
			replyRestList = rService.replyCount(replyList, login, boardId);
		return replyRestList;
	}

	@PostMapping("/replies")
	public int replyInsert(@RequestBody ReplyDTO reply) {
		System.out.println(reply);
		int result = rService.replyInsert(reply);
		if (result == 0)
			return -1;
		return result;
	}

	@PatchMapping("/replies")
	public int replyUpdate(@RequestBody ReplyDTO reply) {
		System.out.println(reply);
		int result = rService.replyUpdate(reply);
		if (result == 0)
			return -1;
		return result;
	}

	@DeleteMapping("/replies/{replyId}")
	public int replyDelete(@PathVariable int replyId) {
		int result = rService.replyDelete(replyId);
		if (result == 0)
			return -1;
		return result;
	}


	@PatchMapping("/replyLikePlus")
	public int replyLikePlus(@RequestBody HashMap<String, Integer> replyMap,
							 HttpSession session) {
		int replyLike = replyMap.get("replyLike");
		System.out.println("현재 좋아요 개수 : " + replyMap.get("replyLike") + " \t " +  replyMap.get("replyId"));
		MemberDTO login = (MemberDTO)session.getAttribute("login");
		System.out.println(login);
		LikeDTO like = new LikeDTO(0, login.getMbrId(), replyMap.get("boardId"), 0, replyMap.get("replyId"));
		boolean isComplete = false;
		replyLike += rService.replyLikePlus(replyMap.get("replyId"));
		isComplete = lService.likeReplyInsert(like);
		System.out.println("좋아요 : " + replyLike + " , 삭제, 삽입 : " + isComplete);
		return replyLike;
	}
	
	@PatchMapping("/replyLikeMinus")
	public int replyLikeMinus(@RequestBody HashMap<String, Integer> replyMap,
							 HttpSession session) {
		int replyLike = replyMap.get("replyLike");
		System.out.println("현재 좋아요 개수 : " + replyMap.get("replyLike") + " \t " +  replyMap.get("replyId"));
		MemberDTO login = (MemberDTO)session.getAttribute("login");
		System.out.println(login);
		LikeDTO like = new LikeDTO(0, login.getMbrId(), replyMap.get("boardId"), 0, replyMap.get("replyId"));
		boolean isComplete = false;
		replyLike += rService.replyLikeMinus(replyMap.get("replyId")) * -1;
		isComplete = lService.likeReplyDelete(like);
		System.out.println("좋아요 : " + replyLike + " , 삭제, 삽입 : " + isComplete);
		return replyLike;
	}
		
}
