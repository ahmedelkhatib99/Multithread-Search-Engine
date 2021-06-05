package com.our;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class Page {
    private String URL;
    private String Title;
    private String Text;
    private final ArrayList<String> hyperlinkList = new ArrayList<>();

    public Page(String URL, Boolean Extract, DBController DB, Counter linkCounter) {
        try {

            this.URL = URL;

            // Fetch Document Data from the internet
            Document document = Jsoup.connect(URL).get();

            setPageTitle(document);
            setPageText(document);

            // If still below 5000 extract all links
            if (Extract)
                setPageLinks(document);

        } catch (HttpStatusException e) {

            //HTTP Error: Log, Decrement and Remove
            System.err.println("Error: " + e.toString() + " Caught in URL: " + URL);
            System.err.println("Error: Removing Link");

            linkCounter.decrement();

            DB.removeFromNewLinks(URL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getHyperlinks() {
        return hyperlinkList;
    }

    public void savePage(DBController DB) {
        DB.addToPages(URL, Title, Text);
    }

    private void setPageTitle(Document document) {
        //Parse the HTML to extract title
        Title = document.title();
    }

    private void setPageText(Document document) {
        //Parse the HTML to extract text
        Text = document.text();
    }

    public String getHost(String link){
        int count = 0;
        for (int i=0 ; i<link.length() ; i++) {
            count = (link.charAt(i) == '/')? count + 1 : count;
            if(count == 3){
                return link.substring(0,i);
            }
        }
        return link;
    }
    public String getPath(String link){
        int count = 0;
        for (int i=0 ; i<link.length() ; i++) {
            count = (link.charAt(i) == '/')? count + 1 : count;
            if(count == 3){
                return link.substring(i);
            }
        }
        return "";
    }

    private void setPageLinks(Document document) {

            Vector<String> arrOfDisallows = new Vector<>();

            try {
                String robotURL = getHost(URL) + getPath(URL) + "/robots.txt";
                Document robot = Jsoup.connect(robotURL).get();
                String[] arrOfStr = robot.text().split(" ");
                boolean userAgent = false;
                for(int i=0;i<arrOfStr.length-1;i++) {
                    if (arrOfStr[i].equals("User-agent:")){
                        userAgent = arrOfStr[i+1].equals("*");
                        continue;
                    }
                    if (userAgent && arrOfStr[i].equals("Disallow:")) {
                        arrOfDisallows.add(arrOfStr[i + 1]);
                    }
                }
            } catch (IOException e) {
                System.err.println("No robot");
            }

            //Parse the HTML to extract links to other URLs
            Elements linksOnPage = document.select("a[href]");

            //Add each extracted URL
            for (Element linkEl : linksOnPage) {
                String link = linkEl.attr("abs:href");

                String[] linkStripped = link.split("[?#]");
                if ((linkStripped[0].startsWith("http://") || linkStripped[0].startsWith("https://")) && (linkStripped[0].endsWith("/") || linkStripped[0].endsWith(".html")) && !(linkStripped[0].contains("linkedin") || linkStripped[0].contains("snapchat")))
                    link = linkStripped[0];
                else
                    continue;

                //Check if in disallowed
                try {
                    java.net.URL url = new URL(link);
                    if(arrOfDisallows.contains(url.getPath())){
                        continue;
                    }
                } catch (IOException e) {
                    System.err.println("No protocol");
                }

                if (!hyperlinkList.contains(link)) {
                    hyperlinkList.add(link);
                }
            }
        }
    }
