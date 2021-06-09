package fr.n7.stl.miniJava.declaration;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.definition.Attribut;
import fr.n7.stl.miniJava.definition.Definition;
import fr.n7.stl.miniJava.methode.Constructor;
import fr.n7.stl.miniJava.methode.Methode;
import fr.n7.stl.miniJava.methode.MethodeSignature;
import fr.n7.stl.miniJava.type.Instanciation;
import fr.n7.stl.miniJava.type.PooType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import java.util.*;

public class ClasseDeclaration implements Type , ContainerDeclaration{

    private List<Constructor> constructorList = new ArrayList<Constructor>(); //Liste des constrcucteurs

    //private PooDeclaration pooDeclaration;    //Notre classe
    private List<Instanciation> implementations;  // les interfaces à implementer
    List<Definition> definitionList;     // les définitions ( methodes, constructeurs, attributs)
    int offset;
    Register register;
	private String name;

    public ClasseDeclaration(String nom, 
            List<Instanciation> implementatioList, List<Definition> definitions){
    	super();
    	this.name = nom;
    	this.implementations = implementatioList;
    	this.definitionList = definitions;
    }
    

    public List<Constructor> getConstructorList() {
    	for (Definition d: definitionList) {
    		Constructor _const = d.getConstructor();
    		if (_const != null)
    			this.constructorList.add(_const);
    	}
        return constructorList;
    }
    public boolean containsAttribute(String name) {
    	for (Definition d: definitionList) {
    		Attribut _const = d.getAttribut();
    		if (_const != null &&_const.getIdent().equals(name)){
    			return true;
    		}
    	}
        return false;
    }

    public void setConstructorList(List<Constructor> constructorList) {
        this.constructorList = constructorList;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }


    public List<Instanciation> getImplementations() {
        return implementations;
    }

    public void setImplementations(List<Instanciation> implementations) {
        this.implementations = implementations;
    }



    @Override
    public boolean equalsTo(Type _other) {
        return false;
    }

    @Override
    public boolean compatibleWith(Type _other) {
        for(Instanciation i : this.implementations){
            if (_other.equalsTo(i.getType())) return true;
        }

        return false;
    }

    @Override
    public Type merge(Type _other) {
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    public boolean collect(HierarchicalScope<Declaration> _scope) {
    	_scope.register(new VariableDeclaration("currentclass", this, null));
    	boolean result = true;
        if(_scope.knows(this.getName())){
            Logger.error("The name " +this.getName()+" of this Class is already used ");
            return false;
        }
        _scope.register(this);
        for (Definition def: this.definitionList) {
        	result = result&&def.collect(_scope);
        }
        for (Constructor constructor: this.constructorList) {
        	//System.out.println(constructor);
        	result = result && constructor.collect(_scope);
        }
        return result;

    }

    public String getName(){
        return this.name;
    }
    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {

        boolean result = true;
        if (implementations!=null) {
        for(Instanciation i : this.implementations){
            result = result && i.resolve(_scope);
            //check if all methodes are implemented in the interface
            InterfaceDeclaration id = (InterfaceDeclaration) i.getDeclaration();
            for(MethodeSignature ms : id.getEntetes()){
            	
                boolean found = false;
                for(Definition d : this.definitionList){
                    if(d.getMethode() != null) {
                        if(d.getMethode().getEntete().equals(ms)) {
                            found = true;
                        }
                    }
                }
                if(!found)
                    Logger.error("Class must implement all methodes\n" + ms + " not implemented !");
            }
        }
        }
        boolean exist = false;
        //verifier que le constructeur existe
        
        for(Definition def : this.definitionList) {
            if(def.getConstructor() != null) {
                exist = true;
            }
        }
        if (!this.getConstructorList().isEmpty()) {
        	exist = true;
            for (Constructor constructor: this.constructorList) {
            	//System.out.println(constructor);
            	result = result && constructor.resolve(_scope);
            }
        }
        // si le constructeur n'existe pas , on crée un qui est vide
        if(!exist) {
            Constructor constructor = new Constructor(new Instanciation(this.getName()),null,null);
            this.constructorList.add(constructor);
            this.definitionList.add(new Definition(constructor));
            System.out.println("Default constructor added successfully for "+ this.name);
        }

        return result;
    }


    public boolean checkType(){
        boolean result = true;
        if (this.implementations != null) {
        for(Instanciation i : this.implementations){
            Type t2 = i.getType();
            if(!t2.equalsTo(PooType.InterfaceType)){
                Logger.error("implements must be used with Interface");
                return false;
            }
        }
        }
        for(Definition d: this.definitionList){
            result = d.checkType() && result;
        }

        return result;
    }
    public List<Definition> getDefinitionList() {
        return definitionList;
    }

    public void setDefinitionList(List<Definition> definitionList) {
        this.definitionList = definitionList;
    }

    public Type getType(){
        return PooType.ClassType;

    }
    public List<Methode> getMethodsOfClass() {
        List<Methode> methods = new LinkedList<Methode>();
        for(Definition i : definitionList)
            if(i.getMethode() instanceof Methode)
                methods.add((Methode) i.getMethode());
        return methods;
    }
    public Fragment getCode(TAMFactory _factory){

        List<Methode> methodeList = new LinkedList<Methode>();
        List<Constructor> constructorList = new LinkedList<Constructor>();

        Fragment frag = _factory.createFragment();
        for(Definition d: this.definitionList)
        {
            if(d.getAttribut() != null )
            {
                frag.append(d.getAttribut().getCode(_factory));
            }
            else if(d.getConstructor() != null)
            {
                frag.append(d.getConstructor().getCode(_factory));

                // frag.add(_factory.createHalt());
            }
            else if(d.getMethode() != null){
                //frag.append(d.getMethode().getCode(_factory));
                if(d.getMethode().getName().equals("main")){
                    frag.append(d.getMethode().getCode(_factory));
                    frag.add(_factory.createHalt());
                }
                else
                     methodeList.add(d.getMethode());
            }

        }
//        if(constructorList.size()!=0)
//            for (Constructor c : constructorList){
//                frag.append(c.getCode(_factory));
//                frag.add(_factory.createHalt());
//            }
        if(methodeList.size() != 0)
        for(Methode m : methodeList){
            frag.append(m.getCode(_factory));
           // frag.add(_factory.createHalt());
        }
        return frag;
    }

    public int allocateMemory(Register register, int offset){

       this.register = register;
       this.offset = offset;

       int size = offset;

        for (Definition d: this.definitionList){
            size += d.allocateMemory(register,size);
        }
        return size;
    }


	public Declaration getAttribute(String name2) {
		for (Definition d: definitionList) {
    		Attribut _const = d.getAttribut();
    		if (_const != null &&_const.getIdent().equals(name2)){
    			return _const;
    		}
    	}
        return null;
	}
	
	public String toString() {
		String _result = "class " + this.getName();
		return _result;
		
	}

}
