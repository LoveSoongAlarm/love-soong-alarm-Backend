package com.lovesoongalarm.lovesoongalarm.domain.chat.application.controller;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatCommandService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatQueryService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomCreateDTO;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.dto.ChatRoomListDTO;
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
    @Operation(summary = "채팅방 생성 및 본인 참여"
            , description = """
            회원이 채팅방을 만들고 본인만 입장합니다.
            상대방은 본인이 채팅을 보낼 때 자동으로 입장됩니다.
            이미 채팅방이 있다면 기존 채팅방의 ID를 반환합니다.
            """)
    @ApiResponse(responseCode = "201", description = "채팅방 생성 및 본인 참여 성공")
    public BaseResponse<ChatRoomCreateDTO.Response> createChatRoom(
            @RequestBody ChatRoomCreateDTO.Request request) {
        //TODO - JWT에서 userId 추출
        Long userId = 1L;
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
    public BaseResponse<ChatRoomListDTO.Response> getChatRoomList(){
        //TODO - JWT에서 userId 추출
        Long userId = 1L;
        return BaseResponse.success(chatQueryService.getChatRoomList(userId));
    }

}
