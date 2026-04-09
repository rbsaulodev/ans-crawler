package com.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class Main {
    static final String BASE_URL = "https://www.gov.br/ans/pt-br/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss";
    static final String DOWNLOADS_BASE = "./Downloads";

    public static void main(String[] args) {
        System.out.println("=== Iniciando Crawler ANS - Modo Robusto ===\n");

        try {
            Document homePage = Jsoup.connect(BASE_URL).userAgent("Mozilla/5.0").timeout(30000).get();

            // TASK 1 - Busca pelo link que tem "padrao-tiss-marco-2026" na URL
            Element linkV = homePage.select("a[href*='padrao-tiss-marco-2026'], a[href*='padrao-tiss-marco_2026']").first();
            if (linkV != null) {
                System.out.println("[Task 1] Entrando na página da versão...");
                Document docV = Jsoup.connect(linkV.attr("abs:href")).get();
                baixarComponente(docV);
            }

            // TASK 2 - Busca pelo link que termina com "historico-das-versoes-dos-componentes-do-padrao-tiss"
            Element linkH = homePage.select("a[href*='historico-das-versoes']").first();
            if (linkH != null) {
                System.out.println("[Task 2] Acessando histórico...");
                Document docH = Jsoup.connect(linkH.attr("abs:href")).get();
                salvarCSV(docH);
            }

            // TASK 3 - Busca pelo link que termina com "tabelas-relacionadas"
            Element linkT = homePage.select("a[href*='tabelas-relacionadas']").first();
            if (linkT != null) {
                System.out.println("[Task 3] Acessando tabelas relacionadas...");
                Document docT = Jsoup.connect(linkT.attr("abs:href")).get();
                baixarErros(docT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void baixarComponente(Document doc) throws Exception {
        Path dir = Paths.get(DOWNLOADS_BASE, "Arquivos_padrao_TISS");
        Files.createDirectories(dir);

        Element link = doc.select("a[href$='.zip']:contains(Comunicação), a[href$='.zip']").first();
        if (link != null) {
            String url = link.attr("abs:href");
            download(url, dir.resolve(url.substring(url.lastIndexOf('/') + 1)));
            System.out.println("-> Task 1 Concluída.");
        }
    }

    private static void salvarCSV(Document doc) throws Exception {
        Path csv = Paths.get(DOWNLOADS_BASE, "historico_versoes.csv");
        Files.createDirectories(csv.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(csv)) {
            bw.write("Competencia;Publicacao;Inicio de Vigencia");
            bw.newLine();
            for (Element tr : doc.select("table tr")) {
                var tds = tr.select("td");
                if (tds.size() >= 3 && validar(tds.get(0).text())) {
                    bw.write(tds.get(0).text() + ";" + tds.get(1).text() + ";" + tds.get(2).text());
                    bw.newLine();
                }
            }
        }
        System.out.println("-> Task 2 Concluída.");
    }

    private static void baixarErros(Document doc) throws Exception {
        Path dir = Paths.get(DOWNLOADS_BASE, "Tabela_Erros");
        Files.createDirectories(dir);
        Element link = doc.select("a[href*='erros'], a[href*='Erro']").first();
        if (link != null) {
            String url = link.attr("abs:href");
            download(url, dir.resolve("tabela_erros.xlsx"));
            System.out.println("-> Task 3 Concluída.");
        }
    }

    private static boolean validar(String t) {
        try {
            String[] p = t.toLowerCase().split("/");
            return Integer.parseInt(p[1]) >= 2016;
        } catch (Exception e) { return false; }
    }

    private static void download(String u, Path p) throws Exception {
        try (InputStream in = new URL(u).openStream()) {
            Files.copy(in, p, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}