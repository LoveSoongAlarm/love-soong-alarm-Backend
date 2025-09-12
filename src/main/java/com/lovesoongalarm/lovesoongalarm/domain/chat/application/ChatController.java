package com.lovesoongalarm.lovesoongalarm.domain.chat.application;

import com.lovesoongalarm.lovesoongalarm.common.BaseResponse;
import com.lovesoongalarm.lovesoongalarm.domain.chat.business.ChatCommandService;
import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.application.ChatRoomCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
@Validated
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatCommandService chatCommandService;

    @PostMapping
    @Operation(summary = "채팅방 생성 및 본인 참여"
            , description = "회원이 채팅방을 만들고 본인만 입장합니다. 상대방은 본인이 채팅을 보낼 때 자동으로 입장됩니다.")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 및 본인 참여 성공")
    public BaseResponse<ChatRoomCreateDTO.Response> createChatRoom(
            @RequestBody ChatRoomCreateDTO.Request request){
        //TODO - JWT에서 userId 추출
        Long userId = 1L;
        return BaseResponse.success(chatCommandService.createChatRoom(userId, request));
    }
}
