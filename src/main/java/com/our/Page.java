package com.our;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class Page {
    private final String URL;
    private String Title;
    private String Text;
    private final ArrayList<String> hyperlinkList = new ArrayList<>();

    public Page(String URL, Boolean Extract) throws IOException {
        Document document = Jsoup.connect(URL).get();

        this.URL = URL;

        setPageTitle(document);
        setPageText(document);
        if (Extract)
            setPageLinks(document);
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

    private void setPageLinks(Document document) {
            System.out.println("Extracting Links from: " + URL);

            Vector<String> arrOfDisallows = new Vector<>();

            try {
                java.net.URL url = new URL(URL);
                String robotURL = url.getProtocol() + "://" + url.getHost() + "/robots.txt";
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
