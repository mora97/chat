package sample.repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Data {

    Scanner contactFileReader, chatFileReader, gameFileReader;
    private List<Person> persons;
    private Person person;
    private File contactFile, chatsFile1, chatsFile2, gamesFile1, gamesFile2;
    private FileWriter contactFileWriter, chatFileWriter1, chatFileWrite2, gamesFileWriter;
    private BufferedWriter contactBufferedWriter, chatsBufferedWriter1, gamesBufferedWriter;
    private List<String> chats;
    private List<String> gamesResult;
    private String baseUrl = "src/sample/repository/data/";

    public Data() {
        try {
            persons = new ArrayList<>();
            contactFile = new File(baseUrl + "users.txt");
//            contactFileReader = new Scanner(contactFile);
            contactFileWriter = new FileWriter(contactFile, true);
            contactBufferedWriter = new BufferedWriter(contactFileWriter);

            chats = new ArrayList<>();
            gamesResult = new ArrayList<>();
        } catch (FileNotFoundException e) {
            System.out.println("no file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("can't write");
            e.printStackTrace();
        }
    }

    public void createFile(String newContactId) throws IOException {
        String chatDirectoryName = baseUrl + "chats/" + newContactId;
        String gameDirectoryName = baseUrl + "games/" + newContactId;

        createDirectory(chatDirectoryName);
        createDirectory(gameDirectoryName);


        List<Person> oldContacs = new ArrayList<>();
        oldContacs = readData();

        for (Person contact : oldContacs) {
            if (!contact.id.equals(newContactId)) {
                String chatFileName = baseUrl + "chats/";
                String gameFileName = baseUrl + "games/";

                createFile(chatFileName, contact.id, newContactId);
                createFile(chatFileName, newContactId, contact.id);
                createFile(gameFileName, contact.id, newContactId);
                createFile(gameFileName, newContactId, contact.id);
            }
        }
    }

    private void createDirectory(String directoryName) throws IOException {
        Path path = Paths.get(directoryName);

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        } else {
            System.out.println("p-exist");
        }
    }

    private void createFile(String chatFileName, String parent, String createdFileName) {
        File file = new File(chatFileName + parent + "/" + createdFileName + ".txt");
        try {
            if (file.createNewFile()) {
                System.out.println("new file created");
            } else {
                System.out.println("already exist");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String name, String id, String email, String password) {
        String details = id + "," + password + "," + name + "," + email;

        try {
            contactBufferedWriter.newLine();
            contactBufferedWriter.write(details);
            contactBufferedWriter.close();

            Person newPerson = new Person();
            newPerson.name = name;
            newPerson.id = id;
            newPerson.email = email;
            newPerson.password = password;

            persons.add(newPerson);

            System.out.println("written");
        } catch (IOException e) {
            System.out.println("second time can't write");
            e.printStackTrace();
        }
    }

    public List<Person> readData() throws FileNotFoundException {
        int i = 0;
        contactFileReader = new Scanner(contactFile);
        persons.removeAll(persons);
        while (contactFileReader.hasNextLine()) {
            String line = contactFileReader.nextLine();
            String[] details = line.split(",");
            if (details.length > 1) {
                String id = details[0];
                String password = details[1];

                Person person = new Person();
                person.id = id;
                person.password = password;

                persons.add(person);
                System.out.println(line);
                i++;
            } else {
                System.out.println(details[0].length());
            }

        }

        contactFileReader.close();

        return persons;
    }

    public void writeChats(String id, String msgToSend, String recipient) {
        try {
            chatsFile1 = new File(baseUrl + "chats/" + id + "/" + recipient + ".txt");
            chatsFile2 = new File(baseUrl + "chats/" + recipient + "/" + id + ".txt");

            chatFileWriter1 = new FileWriter(chatsFile1, true);
            chatsBufferedWriter1 = new BufferedWriter(chatFileWriter1);
            chatsBufferedWriter1.newLine();
            chatsBufferedWriter1.write(id + ": " + msgToSend);
            chatsBufferedWriter1.close();

            chatFileWrite2 = new FileWriter(chatsFile2, true);
            chatsBufferedWriter1 = new BufferedWriter(chatFileWrite2);
            chatsBufferedWriter1.newLine();
            chatsBufferedWriter1.write(id + ": " + msgToSend);
            chatsBufferedWriter1.close();

            System.out.println("chat written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readChats(String receiverId) {
        try {
            chatsFile1 = new File(baseUrl + "chats/" + Person.getLoggedinUserId() + "/" + receiverId + ".txt");
            chatsFile2 = new File(baseUrl + "chats/" + receiverId + "/" + Person.getLoggedinUserId() + ".txt");
            chatFileReader = new Scanner(chatsFile1);

            while (chatFileReader.hasNextLine()) {
                String line = chatFileReader.nextLine();
                if (!line.equals("")) {
                    chats.add(line);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chats;
    }

    public void wirteGameResult(String id, String recipient, String cells, String point) {
        try {
            String saveFormat = id + ":" + cells + "=" + point;

            gamesFile1 = new File(baseUrl + "games/" + id + "/" + recipient + ".txt");
            gamesFile2 = new File(baseUrl + "games/" + recipient + "/" + id + ".txt");

            gamesFileWriter = new FileWriter(gamesFile1, true);
            gamesBufferedWriter = new BufferedWriter(gamesFileWriter);
            gamesBufferedWriter.newLine();
            gamesBufferedWriter.write(saveFormat);
            gamesBufferedWriter.close();

            gamesFileWriter = new FileWriter(gamesFile2, true);
            gamesBufferedWriter = new BufferedWriter(gamesFileWriter);
            gamesBufferedWriter.newLine();
            gamesBufferedWriter.write(saveFormat);
            gamesBufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readGameResult(String receiverId) {
        try {
            gamesFile1 = new File(baseUrl + "games/" + Person.getLoggedinUserId() + "/" + receiverId + ".txt");
            gameFileReader = new Scanner(gamesFile1);

            while (gameFileReader.hasNextLine()) {
                String line = gameFileReader.nextLine();
                gamesResult.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gamesResult;
    }

    public void clearFileData(String loggedinUserId, String selectedContactId) throws IOException {
        FileWriter fwOb = new FileWriter(baseUrl + "games/" + loggedinUserId + "/" + selectedContactId + ".txt", false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);

        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
}
