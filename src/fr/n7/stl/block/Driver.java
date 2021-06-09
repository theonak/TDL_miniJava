package fr.n7.stl.block;


class Driver {

	public static void main(String[] args) throws Exception {
		Parser parser = null;
		if (args.length == 0) {
			//parser = new Parser( "input.txt");
			//parser = new Parser( "tests/test00.mjava");
			//parser = new Parser( "tests/test01.mjava");
			parser = new Parser( "tests/testMethode.mjava");
			//parser = new Parser( "tests/test_methode.mjava");
			parser.parse();
		} else {
			for (String name : args) {
				parser = new Parser( name );
				parser.parse();
			}
		}
	}
}
