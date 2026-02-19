package tutorial;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

public class Bot extends TelegramLongPollingBot {

    private final String token = Dotenv.load().get("TELEGRAM_TOKEN");

    @Override
    public String getBotUsername() {
        return "rottabotti";
    }

    @Override
    public String getBotToken(){
        return token;
    }

    /**
     * Botin käyttämät kuvat
     */
    private final String[] kuvat = {
            "src/main/resources/Kuvat/rotta1.jpeg",
            "src/main/resources/Kuvat/rotta2.jpeg",
            "src/main/resources/Kuvat/rotta3.jpeg",
            "src/main/resources/Kuvat/rotta4.jpeg",
            "src/main/resources/Kuvat/rotta5.jpeg",
            "src/main/resources/Kuvat/rotta6.jpeg",
            "src/main/resources/Kuvat/rotta7.jpeg",
            "src/main/resources/Kuvat/rotta8.jpeg",
            "src/main/resources/Kuvat/rotta9.jpeg",
            "src/main/resources/Kuvat/rotta10.jpeg",
            "src/main/resources/Kuvat/rotta11.jpeg"
    };

    /**
     * Määrittelee mitä tehdään, kun uusi viesti tulee.
     * @param update = viesti.
     */
    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if(msg.isCommand()){
            if(msg.getText().equals("/fakta"))  {       //Jos käyttäjä vastaa /en siirrytään ei-aliohjelmaan
                fakta(id);
            }
            else if (msg.getText().equals("/kuva")) {  //Jos käyttäjä vastaa /joo siirrytään ei-aliohjelmaan
                kuva(id);
            }
            else if (msg.getText().equals("/start")) {  //Jos käyttäjä sanoo /joo siirrytään ei-aliohjelmaan
                baseMessage(id);
            }
        }
        else {
            baseMessage(id);
        }
    }


    /**
     * Lähettää faktan
     * @param id = chatin id
     */
    private void fakta(Long id) {
        String fakta = randomFakta();
        sendText(id, fakta);
    }

    /**
     * Lähettää kuvan
     * @param id = chatin id
     */
    private void kuva(Long id) {
        vastaaKuva(id);
    }


    /**
     * Vastataan kuvalla
     * @param id = Saadun viestin id
     */
    public void vastaaKuva(Long id) {
        InputFile kuva = valitseKuva(); //Arvotaan kuva aliohjelmalla

        SendPhoto lahetaKuva = new SendPhoto(); //Tehdään SendPhoto-olio
        lahetaKuva.setChatId(id.toString()); //Asetetaan ID, mihin chättiin kuva laitetaan
        lahetaKuva.setPhoto(kuva); //Asetetaan lähetettävä kuva

        try {
            execute(lahetaKuva); //Kuvan lähetys
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Arvotaan kuva, joka lähetetään.
     * @return Arvottu kuva
     */
    public InputFile valitseKuva() {
        int montakoKuvaa = kuvat.length;

        //Arvotaan kuva randomilla
        Random randomnro = new Random();
        int nro = randomnro.nextInt(montakoKuvaa);

        //Luodaan tiedosto, asetetaan kuva siihen ja palautetaan tiedosto.
        File kuva = new File(kuvat[nro]);
        InputFile lahetettavaKuva = new InputFile(kuva);
        return lahetettavaKuva;
    }

    /**
     * Viestin lähettäminen
     * @param who Kenelle lähetetään
     * @param what Mitä läheteään
     */
    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Kenelle viesti laitetaan
                .text(what).build();    //Mitä viesti sisältää
        try {
            execute(sm);                        //Viestin lähetys
        } catch (TelegramApiException e) {
            System.out.println("Ei viestejä");
        }
    }

    /**
     * Etsitään yksi random fakta lähetettäväksi
     * @return Palauttaa yhden faktan
     */
    public String randomFakta() {
        List<String> faktat = skanneri();

        Random random = new Random();
        int nro = random.nextInt(faktat.size());
        String fakta = faktat.get(nro);

        return fakta;
    }

    /**
     * Skannataan faktat tekstitiedostosta
     * @return Palauttaa listan faktoista
     */
    public List<String> skanneri() {
        File teksti = new File("src/main/resources/Teksti/Rottafaktat.txt");

        try (Scanner scanner = new Scanner(teksti)) {
            List<String> faktat = new ArrayList<>();

            while (scanner.hasNextLine()) {
                faktat.add(scanner.nextLine());
            }
            return faktat;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
        return null;
    }


    public void baseMessage(Long id){
        sendText(id, "Haluatko rottakuvat vai rottafaktan? \n /kuva \n /fakta");
    }

}
