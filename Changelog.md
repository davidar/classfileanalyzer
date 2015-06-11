# ClassFileAnalyzer 0.8.0 #

  * replaced calls to System.out.println/System.exit with throwing a ClassFormatError exception, which is then caught in the Main class
  * added a ClassFile constructor for passing in class files as a byte array
  * added an option to Builder.buildHeader to remove the ClassFileAnalyzer header
  * removed a few unused imports and local variables