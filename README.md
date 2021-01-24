# CodeGenerator

[中文说明](./doc/readme-zh.md)

An IDEA-plugin for code generation (modify from [lotabout/CodeGenerator](https://github.com/lotabout/CodeGenerator))

**What changes did I make ?**

The original plugin allows you to use velocity templates to generate code. I prefer writing Java code to generate code than the velocity templates.

![image-20210124195309301](./doc/pictures/example.png)

This template will copy the method you selected to the current caret position.

![image-20210124195545218](./doc/pictures/example1.png)

![image-20210124195739128](./doc/pictures/example2.png)

![image-20210124195854049](./doc/pictures/example3.png)

![image-20210124195952945](./doc/pictures/example4.png)



---

> Below is the doc from [lotabout/CodeGenerator/readme](https://github.com/lotabout/CodeGenerator)

As we know, IntelliJ provides useful code generators such as constructors,
getter/setters, equals, hashCode, overrides and delegates, etc. And IntelliJ
allows us to apply customized velocity templates for each generator. But we
cannot add our own generators.

Code Generator is here to help. Two types of generation are supported here
- Members(fields/method) based templates, such as serializers, equals, etc.
- Class based template, such as transformers, converters, etc. Normally new classes are created.

# Installation

1. Search `CodeGenerator` in IDEA plugins
2. Download zip from from Releases

To install a plugin from the disk in IDEA:

1. Open the `Settings/Preferences` dialog box and select `Plugins` on the left pane.
2. On the right pane of the dialog, click the `Install plugin from disk` button.
3. Select the location of the zip file, click OK to continue.
4. Click `Apply` button of the Settings/Preferences dialog.
5. Restart IntelliJ IDEA to activate.

# Usage

1. Go to the `Settings/Preferences > Other Settings > CodeGenerator` to
   create a new generator/template.
2. Right click on your java file, Select `Generate > CodeGenerator > [name of
   your generator]` to run the generator.

According to the settings of your generator, there might be dialogs
asking you to select members or classes required by your generator.

# Pipeline for Generators

Say we want to create a template for generating getters/setters, how will the
user use your template? An example(the default IntelliJ implementation) is:

1. A dialog shows up listing all the fields that doesn't have
   getters/setters yet.
2. The user selects the members.
3. The code is generated using the getter/setter template.

Thus, as a template creator, we need to:

![getters-setters-workflow](https://user-images.githubusercontent.com/1527040/31721488-23332b7a-b3df-11e7-883f-cac8e2be45a5.png)

Here we call it a `pipeline` for generators. Currently two types of user
action are supported:

1. Member selection: generator user can select fields/methods;
2. Class selection: generator user can select a class.

Another example is: you might want to create templates that generate
convertors between two classes, so that you want the user to select the target
class to convert to.

In CodeGenerator, you can create a pipeline with several steps, CodeGenerator
will execute the steps sequencially to collect the context variables and finally
generate the code use the template.

![pipeline](https://user-images.githubusercontent.com/1527040/31721412-e9efcc38-b3de-11e7-99cd-44e3dd37b947.png)

## Member Selection

![member-selection](https://user-images.githubusercontent.com/1527040/31722083-c48779e4-b3e0-11e7-8441-a53a1b76de5d.png)

Templates varies on what members it allows for selection, for example:

- Getters/Setters generator might want the user to select only the fields that
    have no getters/setters implemented.
- Delegate generators might want the user to select the methods that belong to
    the field or its super classes.

Thus CodeGenerator allows generator creators to provide the members to select:

- set `availableMembers` to provide the members to select;
- set `selectedMembers` to select the members initially, not setting it means
    select all available members.

Also after the selection, the template context will add some more variables:

- `fields1`: the selected fields, where `1` is the step postfix;
- `methods1`: the selected methods, if any;
- `members1`: the selected fields/methods.

Here is an example of the context variables:

![context-of-pipeline](https://user-images.githubusercontent.com/1527040/31721898-3629df8e-b3e0-11e7-8ac2-ca75680356cd.png)

Note in the beginning, the `class0` variable refers to the class entry where
user starts code generation.

## Class Selection

![class-selection](https://user-images.githubusercontent.com/1527040/31723071-77492986-b3e3-11e7-8408-3735957fa8d9.png)

Class selection is much simpler that template creator could specify the
initial class to select.

# Acknowledgment
- [CodeMaker](https://raw.githubusercontent.com/x-hansong/CodeMaker): where
    the idea and a part of code comes from.
- [generate-tostring](https://github.com/JetBrains/intellij-community/tree/master/plugins/generate-tostring):
  Official toString generator. A part of the code comes from it.

