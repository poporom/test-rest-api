package com.inside.web.controller;

import com.inside.entity.Message;
import com.inside.entity.User;
import com.inside.model.MessageRequest;
import com.inside.service.MessageService;
import com.inside.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/messages")
@Slf4j
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping("")
    @ResponseStatus(CREATED)
    public List<Message> createMessage(@RequestBody MessageRequest messageRequest) {
        if ("history 10".equals(messageRequest.getMessage())) {
            log.info("process=get-messages");
            return messageService.getLast10Messages();
        } else {
            log.info("process=create-message");
            User user = userService.findByUsername(messageRequest.getName());
            Message message = new Message();
            List<Message> messageList = new ArrayList<>();
            message.setUsers(user);
            message.setMessage(messageRequest.getMessage());
            messageList.add(messageService.createMessage(message));
            return messageList;
        }
    }
}
