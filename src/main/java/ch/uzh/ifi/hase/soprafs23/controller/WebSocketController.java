package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.websockets.dto.ClearMessageDTO;
import ch.uzh.ifi.hase.soprafs23.websockets.dto.DrawingMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/drawing-all")
    @SendTo("/topic/drawing")
    public DrawingMessageDTO sendToAll(@Payload DrawingMessageDTO message) {
        return message;
    }

    @MessageMapping("/drawing-clear")
    @SendTo("/topic/clear")
    public ClearMessageDTO clearDrawing(@Payload ClearMessageDTO message) {
        return message;
    }

}
