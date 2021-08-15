/*
  Copyright 2021, William Glasford

  This file is part of the Weather Station software.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is responsible for drawing the Help Index window.

  Mods:		  09/01/21 Initial Release.
*/
package gui;

import util.ConfigProperties;
import util.Logger;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.*;
import java.util.Enumeration;

class HelpIndexWindow extends JFrame implements TreeSelectionListener
{
  private static final ConfigProperties PROPS = ConfigProperties.instance();
  private final Logger logger = Logger.getInstance();

  private final JTree tree;
  private final JEditorPane helpTextPane;

  /**
   * Constructor.
   */
  public HelpIndexWindow()
  {
    setLayout(new GridLayout(1, 2));

    //Create the nodes.
    DefaultMutableTreeNode top =
      new DefaultMutableTreeNode("Help Index");
    createNodes(top);

    //Create a tree that allows one selection at a time.
    tree = new JTree(top);
    tree.getSelectionModel().setSelectionMode
      (TreeSelectionModel.SINGLE_TREE_SELECTION);

    //Listen for when the selection changes.
    tree.addTreeSelectionListener(this);

    //Create the scroll pane and add the tree to it.
    JScrollPane treeView = new JScrollPane(tree);

    //Create the help text viewing pane.
    helpTextPane = new JEditorPane();
    helpTextPane.setEditable(false);
    JScrollPane helpTextView = new JScrollPane(helpTextPane);

    //Add the scroll panes to a split pane.
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setTopComponent(treeView);
    splitPane.setBottomComponent(helpTextView);

    Dimension minimumSize = new Dimension(100, 50);
    helpTextView.setMinimumSize(minimumSize);
    treeView.setMinimumSize(minimumSize);
    splitPane.setDividerLocation(200);
    splitPane.setPreferredSize(new Dimension(500, 300));

    //Add the split pane to this panel.
    add(splitPane);

    setTitle("Help Index Window");
    setSize(new Dimension(PROPS.getWindowWidth(), PROPS.getWindowHeight()));
    setVisible(true);
  }

  /**
   * Internal method to create the node tree based on the files within the helptext directory.  The names
   * are the hierarchy.
   *
   * @param top The top tree node.
   */
  private void createNodes(DefaultMutableTreeNode top)
  {
    final File folder = new File("helptext");
    File[] files = folder.listFiles();
    if (files == null)
    {
      logger.logData("Help Index: No help files found.");
      return;
    }

    // Loop once for each file, using the file name to build the tree.
    for (File file : files)
    {
      if (file.isFile())
      {
        String filename = file.getName();
        String[] levels = filename.split("\\.");

        // Exclude any files that don't end with "txt".
        String ext = filename.substring(filename.length() - 4);
        if (!ext.equalsIgnoreCase(".txt"))
          continue;

        // Go through levels generating tree node structure.
        DefaultMutableTreeNode currentNode = top;
        Enumeration children = currentNode.children();
        DefaultMutableTreeNode nextChild = null;
        boolean levelFound = false;
        for (int i = 0; i < levels.length; i++)
        {
          // Search through the children looking for this node.
          while (children.hasMoreElements())
          {
            nextChild = (DefaultMutableTreeNode)children.nextElement();
            if (nextChild.toString().equalsIgnoreCase(levels[i]))
            {
              levelFound = true;
              break;
            }
          }

          // If level was not found then create a new node and reset to that node.
          if (!levelFound)
          {
            // If a lower level node was found yet the filename is null then set the filename.
            if (levels[i].equalsIgnoreCase("txt"))
            {
              ((HelpInfo)currentNode.getUserObject()).setFileName(filename);
              break;
            }

            DefaultMutableTreeNode newNode;
            if (levels.length == 2 || i == levels.length - 2)
            {
              newNode = new DefaultMutableTreeNode(new HelpInfo(levels[i], filename));
              currentNode.add(newNode);
              break;
            }
            else
            {
              newNode = new DefaultMutableTreeNode(new HelpInfo(levels[i], null));
              currentNode.add(newNode);
            }
            currentNode = newNode;
            children = currentNode.children();
            levelFound = false;
          }
          else // node was found so reset to this node.
          {
            currentNode = nextChild;
            children = currentNode.children();
            levelFound = false;
          }
        }
      }
    }
  }

  /**
   * A private class that defines a node of the tree.
   */
  private class HelpInfo
  {
    String helpFileName;
    final String name;

    HelpInfo(String nodeName, String filename)
    {
      this.helpFileName = filename;
      this.name = nodeName;
    }

    String getHelpText()
    {
      try(BufferedReader br = new BufferedReader(new FileReader("helptext/" + helpFileName)))
      {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null)
        {
          sb.append(line);
          sb.append(System.lineSeparator());
          line = br.readLine();
        }
        return sb.toString();
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return "";
      }
    }

    public void setFileName(String filename)
    {
      this.helpFileName = filename;
    }

    public String toString()
    {
      return name;
    }
  }

  private void displayText(String text)
  {
    if (text != null)
    {
      helpTextPane.setText(text);
    }
  }

  /**
   * Method called when a tree node is selected.  If selected the text is displayed if this is a leaf node.
   *
   * @param e The event.
   */
  @Override
  public void valueChanged(TreeSelectionEvent e)
  {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    if (node == null)
      return;

    Object nodeInfo = node.getUserObject();
    HelpInfo helpInfo = (HelpInfo)nodeInfo;
    displayText(helpInfo.getHelpText());
  }
}
