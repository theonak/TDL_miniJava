package fr.n7.stl.miniJava.methode;
import java.util.*;
import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.instruction.Assignment;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.Return;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.call.AttributAssignement;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class Methode implements Declaration, Instruction {

    private MethodeSignature entete;
    private Block block;
    boolean isStatic = false;
    public int FinalOrAbstract;
    private String id;
	private boolean isFinal;
    protected int offset ;
    protected  Register register;
    protected String label;
	private ClasseDeclaration classDeclaration;
	private SymbolTable tds;

    public Methode(MethodeSignature entete, Block block, boolean isStatic, boolean isFinal ) {
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.entete = entete;
        this.block = block;
    }
    public Methode(MethodeSignature entete, Block block) {
    	this.isStatic = false;
        this.isFinal = false;
        this.block = block;
        this.entete = entete;
    }
    
    public void setClassOwner(ClasseDeclaration c ) {
    	this.classDeclaration = c;
    	this.block.setClass(c);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getFinalOrAbstract() {
        return FinalOrAbstract;
    }

    public void setFinalOrAbstract(int finalOrAbstract) {
        FinalOrAbstract = finalOrAbstract;
    }


    public MethodeSignature getEntete() {
        return entete;
    }

    public void setEntete(MethodeSignature entete) {
        this.entete = entete;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {
    	this.tds = new SymbolTable(_scope);
        // Représenter la valeur de retour par une variable de nom "return" dont le type est le type de retour de la fonction
        tds.register(new VariableDeclaration("return", this.getType(),null));
        for (ParameterDeclaration p: this.entete.getParameterDeclarations()) {
            tds.register(p);
        }
        return (_scope.contains(this.entete.getName())?false: true) && block.collect(tds);
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        HierarchicalScope<Declaration> tableMethodes = new SymbolTable(_scope);
        if(!tableMethodes.accepts(this)){
            Logger.error("Erreur Resolve Methode Declaration");
            return false;
        }else{
            tableMethodes.register(this);
        }
            return this.block.resolve(tableMethodes);

    }
    
    public Type getType(){
        return this.entete.getType();
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {

        if(!this.getName().equals("main")){
            this.register = _register;
            this.offset = _offset;
            List<ParameterDeclaration> ListOfParamsReversed = new LinkedList<>();

            int paramsize = 1 ;

            if(this.getEntete().getParameterDeclarations() != null){

                ListOfParamsReversed = new LinkedList<>(this.getEntete().getParameterDeclarations());

                Collections.reverse(ListOfParamsReversed);

                for(ParameterDeclaration p : ListOfParamsReversed){
                    paramsize += p.allocateMemory(Register.LB, -1*paramsize);
                }

            }
            if(this.block!=null)
            this.block.allocateMemory(Register.LB, 3);


        }
        else {
            this.register = _register;
            this.offset = _offset;
            this.block.allocateMemory(register, offset);
            return 0;
        }
        return 0;


    }

	@Override
	public boolean checkType() {
		boolean result = true;
		/*if(this.getBlock() != null){
		if(!this.getType().equalsTo(AtomicType.VoidType)) {

			//Type type = this.block.getTypeOfReturn();
			result = result & this.block.checkType() & this.getType().equalsTo(type);
		}else {
			result = result & this.block.checkType();
		}}*/
		return result;
	}

	@Override
	public Fragment getCode(TAMFactory _factory) {
        //get code for main
        this.id = Integer.toString(_factory.createLabelNumber());

        int id = _factory.createLabelNumber();

        if(this.getEntete().getName().equals("main"))
        {

            return this.block.getCode(_factory);
        }
        else {
            Fragment _fragment = _factory.createFragment();

            //By default there is always the pointed object
            int _paramssize = 0;

            if(this.getEntete().getParameterDeclarations() != null)
            for (ParameterDeclaration _parameter : this.getEntete().getParameterDeclarations()) {
                _paramssize += _parameter.getType().length();
            }
            if(this.block!=null)
            _fragment.append(this.block.getCode(_factory));


            if (!this.getEntete().getType().equals(AtomicType.VoidType)) {
                _fragment.add(_factory.createReturn(this.getEntete().getType().length(), _paramssize));
            }


            this.label = "function_" + this.getEntete().getName();
            if(this.block == null){
                _fragment.add(_factory.createPush(0));
            }
            _fragment.addPrefix(this.getStartLabel());
            _fragment.addSuffix(this.getEndLabel());
            Fragment frag_jump = _factory.createFragment();
            frag_jump.add(_factory.createJump(this.getEndLabel()));

            // POP des paramètre (déjà fait par Return)

            frag_jump.append(_fragment);

            return frag_jump;
        }
    }
    public String getStartLabel(){
        if (this.id == null){
            Logger.error("Function label was call before function is declared");
        }

        return "FUNC_"+this.getName()+"_START";
    }
    public String getEndLabel(){
        if (this.id == null){
            Logger.error("Function label was call before function is declared");
        }
        return "FUNC_"+this.getName()+"_END";
    }
	@Override
	public String getName() {
		return this.entete.name;
	}
}
