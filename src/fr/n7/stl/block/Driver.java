package fr.n7.stl.block;


class Driver {

	public static void main(String[] args) throws Exception {
		Parser parser = null;
		if (args.length == 0) {
			//parser = new Parser( "input.txt");
			//parser = new Parser( "tests/test00.mjava");
			//parser = new Parser( "tests/test01.mjava");
			//->parser = new Parser( "tests/test02.mjava");
			//parser = new Parser( "tests/test03.mjava");
			//->parser = new Parser( "tests/test04.mjava");
			//parser = new Parser( "tests/test05.mjava");
			//->parser = new Parser( "tests/test06.mjava");
			//->parser = new Parser( "tests/test07.mjava");
			//->parser = new Parser( "tests/test08.mjava");
			//parser = new Parser( "tests/test09.mjava");
			//->parser = new Parser( "tests/test10.mjava");
			//parser = new Parser( "tests/test11.mjava");
			//->parser = new Parser( "tests/test12.mjava");
			parser = new Parser( "tests/test13.mjava");
			
			
			//parser = new Parser( "tests/testMethode.mjava");
			//parser = new Parser( "tests/testDeclaration.mjava");
			//parser = new Parser( "tests/test_methode.mjava");
			//parser = new Parser( "tests/testType.mjava");
			parser.parse();
		} else {
			for (String name : args) {
				parser = new Parser( name );
				parser.parse();
			}
		}
	}
}
