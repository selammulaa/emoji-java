package com.vdurmont.emoji;

import java.io.*;
import java.util.*;

public class EmojiReader {

    private static List<String> emojiWithFitzPatric = new ArrayList<String>();

    private static List<Emoji>  existingEmojis;

    static {
        existingEmojis = new ArrayList<Emoji>();

        try{

            InputStream stream = EmojiLoader.class.getResourceAsStream("/emojis.json");
            existingEmojis = EmojiLoader.loadEmojis(stream);

        } catch (Exception e){

        }
    }

    public static void main(String args[]) {
        try{

            List<MyEmoji> emojisHexCode  = EmojiReader.emojis();

            List<MyEmoji> emojis  = new ArrayList<MyEmoji>();

            for(MyEmoji e: emojisHexCode){

                if(emojiWithFitzPatric.contains(e.hexValue)){

                    e.supportsFitzPatric = true;

                }

                MyEmoji em = new MyEmoji();
                em.emojiChar = EmojiDataReader.convertToEmoji(e.hexValue);
                em.supportsFitzPatric = e.supportsFitzPatric;
                em.description = e.description;
                em.aliases = e.aliases;
                emojis.add(em) ;
            }

            String jsonString = getJsonString(emojis);

            File file = new File("src/main/resources/emojis-paga.json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.flush();
            fileWriter.close();

        }catch (IOException e){

            e.printStackTrace();

        }
    }

    public static List<MyEmoji> emojis() throws IOException {

        final InputStream is = EmojiReader.class.getClassLoader().getResourceAsStream("emoji-list.txt");
        return EmojiDataReader.getEmojiList(is);

    }


    private static String getJsonString(List<MyEmoji> allEmojis){

        MyEmoji emoji = new MyEmoji();

        int len = allEmojis.size();
        int counter = 0;

        StringBuilder jsonString = new StringBuilder();

        jsonString.append("[\n");

        for (MyEmoji myEmoji: allEmojis) {

            counter++;

            emoji.emojiChar = myEmoji.emojiChar;
            emoji.emoji = convertToUnicode(myEmoji.emojiChar);
            emoji.supportsFitzPatric =myEmoji.supportsFitzPatric;

            jsonString.append("\t{\n");
            jsonString.append("\t\t\"emojiChar\": \""+ emoji.emojiChar +"\",\n");
            jsonString.append("\t\t\"emoji\": \""+ emoji.emoji +"\",\n");

            if(emoji.supportsFitzPatric){

                jsonString.append("\t\t\"supports_fitzpatrick\": "+ "true" +",\n");

            }

            Emoji e = emojiAlreadyFound(emoji.emojiChar);

            if(e != null){

                emoji.description=e.getDescription();
                jsonString.append("\t\t\"description\": \""+ emoji.description +"\",\n");

                emoji.aliases = e.getAliases();
                jsonString.append("\t\t\"aliases\": "+ "[\n");
                int aliasCounter = 0;

                for(String alias: emoji.aliases){

                    aliasCounter++;
                    jsonString.append("\t\t\t\""+  alias+  "\"");

                    if(aliasCounter != emoji.aliases.size()){

                        jsonString.append(",\n");
                    }
                }
                jsonString.append("\n\t\t]" +",\n");

                emoji.tags = e.getTags();
                jsonString.append("\t\t\"tags\": "+ "[\n");
                int tagCounter = 0;

                for(String tag: emoji.tags){

                    tagCounter++;
                    jsonString.append("\t\t\t\""+  tag+  "\"");

                    if(tagCounter != emoji.tags.size()){

                        jsonString.append(",\n");
                    }
                }

                jsonString.append("\n\t\t]");

            }else{

                emoji.description = myEmoji.description;
                jsonString.append("\t\t\"description\": "+ "\""+ emoji.description +"\"" +",\n");

                emoji.aliases = myEmoji.aliases;
                jsonString.append("\t\t\"aliases\": "+ "[\n");
                int aliasCounter = 0;
                for(String alias: emoji.aliases){

                    aliasCounter++;
                    jsonString.append("\t\t\t\""+  alias+  "\"");

                    if(aliasCounter != emoji.aliases.size()){

                        jsonString.append(",\n");
                    }
                }

                jsonString.append("\n\t\t]" +",\n");

                jsonString.append("\t\t\"tags\": "+ "[]");
            }

            jsonString.append("\n\t}");

            if(counter != len){

                jsonString.append(",\n");
            }


        }

        jsonString.append("\n]");
        return jsonString.toString();

    }

    private static Emoji emojiAlreadyFound(String emojiFace){

        for(Emoji e: existingEmojis){

            if(e.getUnicode().equals(emojiFace)){

                return e;
            }
        }
        return null;
    }


    private static String convertToUnicode(String str) {

        StringBuilder b = new StringBuilder(str.length());
        Formatter f = new Formatter(b);

        for (char c : str.toCharArray()) {
            if (c < 128) {

                b.append(c);

            } else {

                f.format("\\u%04x", (int) c);
            }
        }

        return b.toString();
    }

    private static class EmojiDataReader {

        static List<MyEmoji> getEmojiList(final InputStream emojiFileStream) throws IOException {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(emojiFileStream));
            final List<MyEmoji> result = new LinkedList<MyEmoji>();

            String line = reader.readLine();
            String [] lineSplit;

            while (line != null) {

                if (!line.startsWith("#") && !line.startsWith(" ") && !line.startsWith("\n") && line.length() != 0) {

                    lineSplit = line.split(";");

                    if(shouldBeAdded(lineSplit[0].trim())){

                        MyEmoji emoji = new MyEmoji();
                        emoji.hexValue = lineSplit[0].trim();
                        emoji.description = getDescription(lineSplit[1]);
                        emoji.aliases = getAlias(emoji.description);
                        result.add(emoji);
                    }
                }

                line = reader.readLine();
            }

            return result;
        }

        private static String convertToEmoji(final String input) {

            String[] emojiCodepoints = input.split(" ");
            StringBuilder sb = new StringBuilder();

            for (String emojiCodepoint : emojiCodepoints) {

                int codePoint = convertFromCodepoint(emojiCodepoint);
                sb.append(Character.toChars(codePoint));
            }

            return sb.toString();
        }

        private static int convertFromCodepoint(String emojiCodepointAsString) {

            return Integer.parseInt(emojiCodepointAsString, 16);

        }

        private static boolean shouldBeAdded(String input){

            List<String> lineSplit = new ArrayList<String>();
            Collections.addAll(lineSplit, input.split(" "));
            String[] fitzPatricCode = {"1F3FB", "1F3FC", "1F3FD", "1F3FE", "1F3FF"};

            for(String fp: fitzPatricCode){

                if(lineSplit.contains(fp)){

                    handleFitzPatric(lineSplit, fp);
                    return false;
                }
            }

            return true;
        }


        private static List<String> handleFitzPatric(List<String> input, String fitzPatricCode){

            int index = input.indexOf(fitzPatricCode);
            if(index != -1){

                input.remove(index);
                emojiWithFitzPatric.add(buildAString(input).trim());
                return input;
            }

            return input;
        }

        private static String buildAString(List<String> input){

            StringBuilder sb = new StringBuilder();

            for(String i: input){

                sb.append(i + " ");
            }

            return sb.toString();
        }

        private static String getDescription(String input){

            String[] splitLine = input.split("#")[1].split(" ");
            List<String> descriptionList = new ArrayList<String>();
            Collections.addAll(descriptionList, splitLine);

            if(descriptionList.size() > 2){

                descriptionList.remove(0);
                descriptionList.remove(0);
                return buildAString(descriptionList).trim();
            }

            return "";

        }

        private static List<String> getAlias(String description){

            List<String> alias = new ArrayList<String>();
            String alias1 = description.replaceAll(" ", "_");
            alias.add(alias1);

            String alias2=null;

            if(description.contains("woman")){

                alias2 = description.replaceAll("woman","female").replaceAll(" ", "_");
                alias.add(alias2);
            }

            if(description.contains("man") && !description.contains("woman")){

                alias2 = description.replaceAll("man","male").replaceAll(" ", "_");
                alias.add(alias2);
            }

            return alias;
        }

    }

    private static class MyEmoji{

        String emojiChar;
        String emoji;
        String description;
        boolean supportsFitzPatric;
        List<String> aliases;
        List<String> tags;
        String hexValue;
    }
}


