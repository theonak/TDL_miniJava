package fr.n7.stl.miniJava.methode;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.miniJava.declaration.ContainerDeclaration;
import fr.n7.stl.miniJava.definition.Definition;
import fr.n7.stl.miniJava.type.Instanciation;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import java.util.*;

public class Constructor implements Declaration, Instruction {

    Instanciation instanciation;
    List<ParameterDeclaration> parameterDeclarationList;
    Block bloc;
    String label;
    private Register register;
    private  int offset ;
    int allocation_length;
    private String id;


    public Constructor(Instanciation instanciation, List<ParameterDeclaration> parameterDeclarations, Block bloc){
        super();
        this.instanciation= instanciation;
        this.parameterDeclarationList = parameterDeclarations;
        this.bloc = bloc;
    }
    public Constructor (String nom, List<ParameterDeclaration> parameterDeclarations, Block bloc){
    	this.label = nom;
    	this.parameterDeclarationList = parameterDeclarations;
        this.bloc = bloc;
    }
    public String toString() {
    	return "construct " + this.instanciation.getName() + "(" + this.parameterDeclarationList + ") { " + "/n" + this.bloc + "}";
    }

    public Instanciation getInstanciation() {
        return instanciation;
    }

    public void setInstanciation(Instanciation instanciation) {
        this.instanciation = instanciation;
    }

    public List<ParameterDeclaration> getParameterDeclarationList() {
        return parameterDeclarationList;
    }

    public void setParameterDeclarationList(List<ParameterDeclaration> parameterDeclarationList) {
        this.parameterDeclarationList = parameterDeclarationList;
    }

    public Block getBloc() {
        return bloc;
    }

    public void setBloc(Block bloc) {
        this.bloc = bloc;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getAllocation_length() {
        return allocation_length;
    }

    public void setAllocation_length(int allocation_length) {
        this.allocation_length = allocation_length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName(){
        return this.instanciation.getName();
    }

    @Override
    public Type getType(){
        throw new SemanticsUndefinedException("Semantics getCode is not implemented in PointerAccess.");
    }

    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {
        return this.bloc.collect(_scope);
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        if(_scope.knows(this.getName())){
            ContainerDeclaration pod = (ContainerDeclaration)_scope.get(this.getName());
            this.instanciation.setDeclaration(pod);
            if(this.getName().equals(this.instanciation.getDeclaration().getName())) {
                boolean result = true;
                HierarchicalScope<Declaration> scope = new SymbolTable(_scope);
                if (this.parameterDeclarationList != null)
                    for (ParameterDeclaration param : this.parameterDeclarationList) {
//                       System.out.println("from Constructor : " +param.getName());
                        result = param.resolve(scope) && result;

                    }
                boolean b = true;
                if (this.bloc != null) {
                    b = this.bloc.resolve(_scope);

                    return this.bloc.resolve(scope) && result && b;
                }else
                {return true;}

            }else {
                Logger.error("Constructor must have same name as the class");
            }
        }else{
            Logger.error("Constructor must have same name as the class");

        }

        return false;
    }
    @Override
    public int allocateMemory(Register _register, int _offset) {
        this.register = _register;
        this.offset = _offset;
        int i =0;
        if(parameterDeclarationList!=null){
        for(ParameterDeclaration p : this.parameterDeclarationList) {
            i -= p.getType().length();
        }
        for (ParameterDeclaration p : this.parameterDeclarationList) {
            p.allocateMemory(Register.LB, i);
            p.setOffset(i);
            p.setRegister(Register.LB);
            i += p.getType().length();

   //         System.out.println(    "parameters : "+this.parameterDeclarationList.get(-i)+" offset : "+this.parameterDeclarationList.get(-i).getOffset());


        }}
        if(this.bloc!=null)
           this.bloc.allocateMemory(Register.LB,3);
        else
            return 0;

        return 0;

    }

 //   @Override
//    public int allocateMemory(Register _register, int _offset) {
//        this.register = _register;
//        this.offset = _offset;
//
//        //Calcul de l'offset pour les params, LB avec (-)
//
//        this.allocation_length = 0;
//        if (this.parameterDeclarationList != null){
//            for (int i = this.parameterDeclarationList.size() - 1; i >= 0; i--) {
//                ParameterDeclaration p = this.parameterDeclarationList.get(i);
//                //p.setOffset(-1 * this.allocation_length);
//                this.allocation_length += p.getType().length();
//                this.allocation_length += p.allocateMemory(Register.LB, -1*this.allocation_length);
//
//                //!!!! voir functionDeclaration et setOffest de Parameter declaration au cas ou mettre set offset = this.offset
//
//            }
//
//    }
//        this.bloc.allocateMemory(Register.LB,-1*this.allocation_length);
//
//        return 0;
//    }

	@Override
	public boolean checkType() {
        if(this.bloc!=null){
		    return this.bloc.checkType();}
        else return true;
	}


	@Override
	public Fragment getCode(TAMFactory _factory) {

        this.id = Integer.toString(_factory.createLabelNumber());
        Fragment frag = _factory.createFragment();

        // Placer le code de la fonction (en indiquant la taille du type pour que le block ne l'efface pas
        int id = _factory.createLabelNumber();


        if(this.bloc!=null)
             frag.append(this.bloc.getCode(_factory));
        else{
            frag.add(_factory.createPush(0));
        }

        // Placer un return s'il n'y en a pas (type void seulement)
//        if(this.bloc == null){
//            frag.add(_factory.createPush(0));
//        }
        // Placer les etiquettes


            frag.addPrefix(this.getStartLabel());

        frag.add(_factory.createReturn(0, 0));
        frag.addSuffix(this.getEndLabel());
            // Placer un jump au tout début pour eviter la fonction
            Fragment frag_jump = _factory.createFragment();
            frag_jump.add(_factory.createJump(this.getEndLabel()));

            // POP des paramètre (déjà fait par Return)

            frag_jump.append(frag);
            return frag_jump;




	}

    public String getStartLabel(){
        if (this.id == null){
            Logger.error("Function label was call before function is declared");
        }

        return "FUNC_"+this.instanciation.getName()+"_START";
    }
    public String getEndLabel(){
        if (this.id == null){
            Logger.error("Function label was call before function is declared");
        }
        return "FUNC_"+this.instanciation.getName()+"_"+this.id+"_END";
    }
}
