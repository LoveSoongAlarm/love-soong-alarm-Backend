package com.lovesoongalarm.lovesoongalarm.domain.chat.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.common.annotation.UserId;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatCommandService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatQueryService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomDetailDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.application.dto.ChatMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@Validated
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    @PostMapping
    @Operation(summary = "채팅방 생성 및 본인 참여",
            description = """
            회원이 채팅방을 만들고 본인만 입장합니다.
            상대방은 본인이 채팅을 보낼 때 자동으로 입장됩니다.
            이미 채팅방이 있다면 기존 채팅방의 ID를 반환합니다.
            """)
    @ApiResponse(responseCode = "201", description = "채팅방 생성 및 본인 참여 성공")
    public BaseResponse<ChatRoomCreateDTO.Response> createChatRoom(
            @UserId Long userId,
            @RequestBody ChatRoomCreateDTO.Request request) {
        return BaseResponse.success(chatCommandService.createChatRoom(userId, request));
    }

    @GetMapping
    @Operation(summary = "채팅방 목록 조회",
            description = """
            채팅방 목록을 조회합니다.
            마지막 메시지를 내가 보냈는지와 상대방이 보냈는지를 boolean 형태로 반환하고,
            그 메시지의 읽음 여부도 필드로 구분합니다.
            """)
    @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공")
    public BaseResponse<ChatRoomListDTO.Response> getChatRoomList(@UserId Long userId){
        return BaseResponse.success(chatQueryService.getChatRoomList(userId));
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "채팅방 상세 조회 (초기 진입)",
            description = """
            채팅방에 처음 진입할 때 상대방의 프로필 정보를 조회합니다.
            상대방의 기본 정보(닉네임, 이모지, 나이, 전공, 관심사)를 반환합니다.
            """)
    @ApiResponse(responseCode = "200", description = "채팅방 상세 조회 성공")
    public BaseResponse<ChatRoomDetailDTO.Response> getChatRoomDetail(
            @UserId Long userId,
            @PathVariable Long roomId) {
        return BaseResponse.success(chatQueryService.getChatRoomDetail(userId, roomId));
    }

    @GetMapping("/rooms/{roomId}/previous-messages")
    @Operation(summary = "채팅방 과거 메시지 조회",
            description = """
            채팅방의 과거 메시지를 페이징으로 조회합니다.
            커서 기반 페이징을 사용하여 성능을 최적화했습니다.
            
            이전 응답의 oldestMessageId를 lastMessageId로 사용
            
            **주의사항:**
            - size는 1~100 사이의 값만 허용됩니다
            - 메시지는 최신순(내림차순)으로 정렬됩니다
            """)
    @ApiResponse(responseCode = "200", description = "과거 메시지 조회 성공")
    public BaseResponse<ChatMessageDTO.ListResponse> getChatRoomMessages(
            @UserId Long userId,
            @PathVariable Long roomId,
            @RequestBody ChatMessageDTO.Request request) {
        return BaseResponse.success(chatQueryService.getChatRoomMessages(userId, roomId, request));
    }
}
