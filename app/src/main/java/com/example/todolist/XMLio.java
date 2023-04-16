package com.example.todolist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLio {

    private ArrayList<task> tasksUncompleted;
    private ArrayList<task> tasksCompleted;

    public XMLio(ArrayList<task> tasksUncompleted, ArrayList<task> tasksCompleted){
        this.tasksUncompleted = tasksUncompleted;
        this.tasksCompleted = tasksCompleted;
    }

    public void saveToXML(File xml) {
        Document dom;
        Element e = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            Element tasks = dom.createElement("allTasks");
            Element task;

            for(int i = 0; i < tasksUncompleted.size(); i++) {
                task = dom.createElement("task");
                writeElement(i, e, tasksUncompleted, task, dom);
                tasks.appendChild(task);
            }


            for(int i = 0; i < tasksCompleted.size(); i++) {
                task = dom.createElement("task");
                writeElement(i, e, tasksCompleted, task, dom);
                tasks.appendChild(task);
            }
            dom.appendChild(tasks);


            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(xml)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    public void writeElement(int i, Element e, ArrayList<task> taskList, Element task, Document dom){
        e = dom.createElement("Name");
        e.appendChild(dom.createTextNode(taskList.get(i).getName()));
        task.appendChild(e);

        e = dom.createElement("Description");
        e.appendChild(dom.createTextNode(taskList.get(i).getDescription()));
        task.appendChild(e);

        e = dom.createElement("Completed");
        e.appendChild(dom.createTextNode(String.valueOf(taskList.get(i).getCompleted())));
        task.appendChild(e);

        e = dom.createElement("Date");
        e.appendChild(dom.createTextNode(taskList.get(i).getDateAsString()));
        task.appendChild(e);

        e = dom.createElement("Priority");
        e.appendChild(dom.createTextNode(String.valueOf(taskList.get(i).getPriority())));
        task.appendChild(e);
    }

    public boolean readXML(File xml) {
        String name;
        String description;
        Boolean completed;
        String date;
        int priority;

        Document dom;
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the
            // XML file
            dom = db.parse(xml);

            Element doc = dom.getDocumentElement();
            NodeList nodeList = doc.getElementsByTagName("task");
            NodeList nameNodeList = doc.getElementsByTagName("Name");
            NodeList descriptionNodeList = doc.getElementsByTagName("Description");
            NodeList completedNodeList = doc.getElementsByTagName("Completed");
            NodeList dateNodeList = doc.getElementsByTagName("Date");
            NodeList priorityNodeList = doc.getElementsByTagName("Priority");

            task t;

            for(int i = 0; i < nodeList.getLength(); i++){
                if (nameNodeList.getLength() > 0 && nameNodeList.item(0).hasChildNodes()) {
                    name = nameNodeList.item(i).getFirstChild().getNodeValue();
                }
                else{
                    name = "";
                }
                if (descriptionNodeList.getLength() > 0 && descriptionNodeList.item(0).hasChildNodes()) {
                    description = descriptionNodeList.item(i).getFirstChild().getNodeValue();
                }
                else{
                    description = "";
                }
                if (completedNodeList.getLength() > 0 && completedNodeList.item(0).hasChildNodes()) {
                    completed = Boolean.valueOf(completedNodeList.item(i).getFirstChild().getNodeValue());
                }
                else{
                    completed = false;
                }
                if (dateNodeList.getLength() > 0 && dateNodeList.item(0).hasChildNodes()) {
                    date = dateNodeList.item(i).getFirstChild().getNodeValue();
                }
                else{
                    date = "01/01/2022";
                }
                if (priorityNodeList.getLength() > 0 && priorityNodeList.item(0).hasChildNodes()) {
                    priority = Integer.valueOf(priorityNodeList.item(i).getFirstChild().getNodeValue());
                }
                else{
                    priority = 0;
                }

                t = new task(name, description, completed, date, priority);
                if(completed){
                    tasksCompleted.add(t);
                }
                else{
                    tasksUncompleted.add(t);
                }
            }
            return true;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return false;
    }

    private String getTextValue(Element doc, String tag) {
        String value = "";
        NodeList nl;
        nl = doc.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }


}
