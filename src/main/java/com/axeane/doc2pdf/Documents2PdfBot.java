package com.axeane.doc2pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Documents2PdfBot extends TelegramLongPollingBot {
  private Converter converter = new Converter();

  @Override
  public String getBotToken() {
    return "Your token";
  }

  @Override
  public void onUpdateReceived(Update update) {
    try {
      if (update.hasMessage()) {
        long chatId = update.getMessage().getChatId();

        if (update.getMessage().hasText()) {
          execute(new SendMessage()
              .setChatId(chatId)
              .setText("Please send me a docx file"));
        }
        if (update.getMessage().getDocument().getFileName().endsWith(".docx")) {
          String fileId = update.getMessage().getDocument().getFileId();

          //Get url of file (Output is JSON)
          URL target = new URL("https://api.telegram.org/bot" +
              getBotToken() + "/getFile?file_id=" + fileId);

          //Deserialization
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(target.openStream()));
          String res = reader.readLine();
          JSONObject jResult = new JSONObject(res);
          JSONObject path = jResult.getJSONObject("result");
          String filePath = path.getString("file_path");

          //Direct link of document
          URL directLink = new URL("https://api.telegram.org/file/bot" +
              getBotToken() + "/" + filePath);
          InputStream inputStream = directLink.openStream();

          File docx = new File("Target.docx");
          File pdf = new File("Converted.pdf");

          //Copy file from URL to local file
          Files.copy(inputStream, Paths.get(docx.getPath()),
              StandardCopyOption.REPLACE_EXISTING);

          //Converting
          execute(new SendMessage().setChatId(chatId).setText("Converting..."));
          converter.convert("Target.docx", "Converted.pdf");

          reader.close();
          inputStream.close();

          //Sending file to bot
          SendDocument sendDocument = new SendDocument();
          sendDocument.setChatId(chatId)
              .setDocument(pdf)
              .setCaption("Converted by @Documents2PdfBot");
          execute(sendDocument);
        } else {
          execute(new SendMessage()
              .setChatId(chatId)
              .setText("Wrong file extension"));
        }
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  @Override
  public String getBotUsername() {
    return "Documents2PdfBot";
  }
}
