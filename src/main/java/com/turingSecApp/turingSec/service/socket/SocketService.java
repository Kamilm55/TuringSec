package com.turingSecApp.turingSec.service.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.turingSecApp.turingSec.model.entities.message.FileMessageInReport;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SocketService {

    private SocketIOServer socketIOServer;

    public SocketService(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());

        socketIOServer.addEventListener("send_message_str", StringMessageInReport.class, onStrMessageReceived()); // base/parent class Message
        //socketIOServer.addEventListener("send_message_file", FileMessageInReport.class, onFileMessageReceived());
    }

    private DataListener<StringMessageInReport> onStrMessageReceived() {
        return (socketIOClient, data, ackSender) -> {
          log.info(String.format("Sender client id: %s -> msg: %s at %s",socketIOClient.getSessionId().toString(),data.getContent(), data.getCreatedAt().toString()));

        };
    }

//    private DataListener<FileMessageInReport> onFileMessageReceived() {
//        return null;
//    }


    //
    private ConnectListener onConnected() {
        return socketIOClient -> {
            //todo: user info must be logged detailed
            // log.info(String.format("User with username:%s started the socket"));
            log.info(String.format("SocketID: %s connected!", socketIOClient.getSessionId().toString()));
        };
    }

    private DisconnectListener onDisconnected() {
        return socketIOClient -> {
          log.info(String.format("SocketID: %s disconnected!", socketIOClient.getSessionId().toString()));
        };
    }


}
