package fr.n7.stl.miniJava.type;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.miniJava.declaration.ContainerDeclaration;
import fr.n7.stl.miniJava.declaration.InterfaceDeclaration;
import fr.n7.stl.miniJava.definition.Attribut;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import java.util.*;

public class Instanciation implements Type, Expression {

    String name;
    List<Instanciation> instanciations;
    ContainerDeclaration declaration;
    private String label;

    public Instanciation(String name, List<Instanciation> instanciations) {
        super();
        this.name = name;
        this.instanciations = instanciations;
    }

    public Instanciation(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Instanciation> getInstanciations() {
        return instanciations;
    }

    public void setInstanciations(List<Instanciation> instanciations) {
        this.instanciations = instanciations;
    }

    public ContainerDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(ContainerDeclaration declaration) {
        this.declaration = declaration;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {
        return false;
    }

    @Override
    public Type getType() {
        return this.declaration.getType();
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {

        ClasseDeclaration cld = (ClasseDeclaration) this.declaration;
        Fragment _result = _factory.createFragment();
        _result.add(_factory.createLoad(
                cld.getRegister(),
                cld.getOffset(),
                this.length()));
        _result.addComment(this.toString());
        return _result;
    }

    @Override
    public boolean equalsTo(Type _other) {
        return false;
    }

    @Override
    public boolean compatibleWith(Type _other) {
		if(_other instanceof Instanciation){
			Instanciation other = (Instanciation) _other;
			
			//System.out.println("Instantiation: compatibleWith, " + other.getName() + " " + ((ClasseDeclaration)other.getDeclaration()).getImplementations() );
			
			if(this.getName().equals(other.getName())){
				return true;
			}
			
			//System.out.println("Instantiation : compatibleWith, " + this.declaration.getName() + " <->? " + other.getType() + " " + (other.getType() instanceof PooType));
			if (this.declaration instanceof ClasseDeclaration){
				for(Instanciation i : ((ClasseDeclaration) this.declaration).getImplementations()){
						if(this.declaration.getName().contentEquals(i.getName()))
							return true;
				}
			}
			else if (this.declaration instanceof InterfaceDeclaration){
				for(Instanciation i : ((ClasseDeclaration)other.getDeclaration()).getImplementations()){
					//System.out.println("Instanciation : compatibleWith, Type(declaration)=InterfaceDeclaration, " + this.getName() +" "+ i.getName() );
					if(this.getName().contentEquals(i.getName())) {
						return true;
					}
				}
			}

				
				
				//for (InterfaceType implInterfaces : ((PooType)_other)) {
				

				
				//System.out.println("Hello " + ((InterfaceDeclaration) this.declaration).getType().compatibleWith(((ClassType)_other).getType()));

			
		} else if(_other instanceof PooType) {
			return this.getType().compatibleWith(_other);
		}
		
		return false;
    }

    @Override
    public Type merge(Type _other) {
        return null;
    }

    @Override
    public int length() {
        return this.getType().length();
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
		if(_scope.knows(this.name)){
			this.declaration = (ContainerDeclaration) _scope.get(this.name);
			
			boolean result = true;
			if( this.instanciations != null){
				for(Instanciation i : this.instanciations){
					result = result && i.resolve(_scope);
				}}

			return result;
		}
		else{
			Logger.error("Class  " +this.name+" not found ...");
			return false;
		}
    }
}
