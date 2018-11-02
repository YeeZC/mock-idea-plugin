package me.zyee;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiTypeParameterList;
import com.intellij.psi.ResolveState;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/2
 */
public class CustomPsiClass implements PsiClass {

    private PsiClass psiClass;
    private int order;

    public CustomPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public CustomPsiClass(PsiClass psiClass, int order) {
        this.psiClass = psiClass;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return psiClass.getQualifiedName();
    }

    @Override
    public boolean isInterface() {
        return psiClass.isInterface();
    }

    @Override
    public boolean isAnnotationType() {
        return psiClass.isAnnotationType();
    }

    @Override
    public boolean isEnum() {
        return psiClass.isEnum();
    }

    @Nullable
    @Override
    public PsiReferenceList getExtendsList() {
        return psiClass.getExtendsList();
    }

    @Nullable
    @Override
    public PsiReferenceList getImplementsList() {
        return psiClass.getImplementsList();
    }

    @NotNull
    @Override
    public PsiClassType[] getExtendsListTypes() {
        return psiClass.getExtendsListTypes();
    }

    @NotNull
    @Override
    public PsiClassType[] getImplementsListTypes() {
        return psiClass.getImplementsListTypes();
    }

    @Nullable
    @Override
    public PsiClass getSuperClass() {
        return psiClass.getSuperClass();
    }

    @NotNull
    @Override
    public PsiClass[] getInterfaces() {
        return psiClass.getInterfaces();
    }

    @NotNull
    @Override
    public PsiClass[] getSupers() {
        return psiClass.getSupers();
    }

    @NotNull
    @Override
    public PsiClassType[] getSuperTypes() {
        return psiClass.getSuperTypes();
    }

    @NotNull
    @Override
    public PsiField[] getFields() {
        return psiClass.getFields();
    }

    @NotNull
    @Override
    public PsiMethod[] getMethods() {
        return psiClass.getMethods();
    }

    @NotNull
    @Override
    public PsiMethod[] getConstructors() {
        return psiClass.getConstructors();
    }

    @NotNull
    @Override
    public PsiClass[] getInnerClasses() {
        return psiClass.getInnerClasses();
    }

    @NotNull
    @Override
    public PsiClassInitializer[] getInitializers() {
        return psiClass.getInitializers();
    }

    @NotNull
    @Override
    public PsiField[] getAllFields() {
        return psiClass.getAllFields();
    }

    @NotNull
    @Override
    public PsiMethod[] getAllMethods() {
        return psiClass.getAllMethods();
    }

    @NotNull
    @Override
    public PsiClass[] getAllInnerClasses() {
        return psiClass.getAllInnerClasses();
    }

    @Nullable
    @Override
    public PsiField findFieldByName(String s, boolean b) {
        return psiClass.findFieldByName(s, b);
    }

    @Nullable
    @Override
    public PsiMethod findMethodBySignature(PsiMethod psiMethod, boolean b) {
        return psiClass.findMethodBySignature(psiMethod, b);
    }

    @NotNull
    @Override
    public PsiMethod[] findMethodsBySignature(PsiMethod psiMethod, boolean b) {
        return psiClass.findMethodsBySignature(psiMethod, b);
    }

    @NotNull
    @Override
    public PsiMethod[] findMethodsByName(String s, boolean b) {
        return psiClass.findMethodsByName(s, b);
    }

    @NotNull
    @Override
    public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(String s, boolean b) {
        return psiClass.findMethodsAndTheirSubstitutorsByName(s, b);
    }

    @NotNull
    @Override
    public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
        return psiClass.getAllMethodsAndTheirSubstitutors();
    }

    @Nullable
    @Override
    public PsiClass findInnerClassByName(String s, boolean b) {
        return psiClass.findInnerClassByName(s, b);
    }

    @Nullable
    @Override
    public PsiElement getLBrace() {
        return psiClass.getLBrace();
    }

    @Nullable
    @Override
    public PsiElement getRBrace() {
        return psiClass.getRBrace();
    }

    @Nullable
    @Override
    public PsiIdentifier getNameIdentifier() {
        return psiClass.getNameIdentifier();
    }

    @Override
    public PsiElement getScope() {
        return psiClass.getScope();
    }

    @Override
    public boolean isInheritor(@NotNull PsiClass psiClass, boolean b) {
        return psiClass.isInheritor(psiClass, b);
    }

    @Override
    public boolean isInheritorDeep(PsiClass psiClass, @Nullable PsiClass psiClass1) {
        return psiClass.isInheritorDeep(psiClass, psiClass1);
    }

    @Nullable
    @Override
    public PsiClass getContainingClass() {
        return psiClass.getContainingClass();
    }

    @NotNull
    @Override
    public Collection<HierarchicalMethodSignature> getVisibleSignatures() {
        return psiClass.getVisibleSignatures();
    }

    @Nullable
    @Override
    public String getName() {
        return psiClass.getName();
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return psiClass.setName(s);
    }

    @Override
    public boolean isDeprecated() {
        return psiClass.isDeprecated();
    }

    @Override
    public boolean hasTypeParameters() {
        return psiClass.hasTypeParameters();
    }

    @Nullable
    @Override
    public PsiTypeParameterList getTypeParameterList() {
        return psiClass.getTypeParameterList();
    }

    @NotNull
    @Override
    public PsiTypeParameter[] getTypeParameters() {
        return psiClass.getTypeParameters();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return psiClass.getPresentation();
    }

    @Nullable
    @Override
    public PsiDocComment getDocComment() {
        return psiClass.getDocComment();
    }

    @Nullable
    @Override
    public PsiModifierList getModifierList() {
        return psiClass.getModifierList();
    }

    @Override
    public boolean hasModifierProperty(@NotNull String s) {
        return psiClass.hasModifierProperty(s);
    }

    @Override
    public void navigate(boolean b) {
        psiClass.navigate(b);
    }

    @Override
    public boolean canNavigate() {
        return psiClass.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return psiClass.canNavigateToSource();
    }

    @NotNull
    @Override
    public Project getProject() throws PsiInvalidElementAccessException {
        return psiClass.getProject();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return psiClass.getLanguage();
    }

    @Override
    public PsiManager getManager() {
        return psiClass.getManager();
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return psiClass.getChildren();
    }

    @Override
    public PsiElement getParent() {
        return psiClass.getParent();
    }

    @Override
    public PsiElement getFirstChild() {
        return psiClass.getFirstChild();
    }

    @Override
    public PsiElement getLastChild() {
        return psiClass.getLastChild();
    }

    @Override
    public PsiElement getNextSibling() {
        return psiClass.getNextSibling();
    }

    @Override
    public PsiElement getPrevSibling() {
        return psiClass.getPrevSibling();
    }

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return psiClass.getContainingFile();
    }

    @Override
    public TextRange getTextRange() {
        return psiClass.getTextRange();
    }

    @Override
    public int getStartOffsetInParent() {
        return psiClass.getStartOffsetInParent();
    }

    @Override
    public int getTextLength() {
        return psiClass.getTextLength();
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int i) {
        return psiClass.findElementAt(i);
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int i) {
        return psiClass.findReferenceAt(i);
    }

    @Override
    public int getTextOffset() {
        return psiClass.getTextOffset();
    }

    @Override
    public String getText() {
        return psiClass.getText();
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return psiClass.textToCharArray();
    }

    @Override
    public PsiElement getNavigationElement() {
        return psiClass.getNavigationElement();
    }

    @Override
    public PsiElement getOriginalElement() {
        return psiClass.getOriginalElement();
    }

    @Override
    public boolean textMatches(@NotNull CharSequence charSequence) {
        return psiClass.textMatches(charSequence);
    }

    @Override
    public boolean textMatches(@NotNull PsiElement psiElement) {
        return psiClass.textMatches(psiElement);
    }

    @Override
    public boolean textContains(char c) {
        return psiClass.textContains(c);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor psiElementVisitor) {
        psiClass.accept(psiElementVisitor);
    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor psiElementVisitor) {
        psiClass.accept(psiElementVisitor);
    }

    @Override
    public PsiElement copy() {
        return psiClass.copy();
    }

    @Override
    public PsiElement add(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return psiClass.add(psiElement);
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
        return psiClass.addBefore(psiElement, psiElement1);
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
        return psiClass.addAfter(psiElement, psiElement1);
    }

    @Override
    public void checkAdd(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        psiClass.checkAdd(psiElement);
    }

    @Override
    public PsiElement addRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {
        return psiClass.addRange(psiElement, psiElement1);
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement psiElement, @NotNull PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
        return psiClass.addRangeBefore(psiElement, psiElement1, psiElement2);
    }

    @Override
    public PsiElement addRangeAfter(PsiElement psiElement, PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
        return psiClass.addRangeAfter(psiElement, psiElement1, psiElement2);
    }

    @Override
    public void delete() throws IncorrectOperationException {
        psiClass.delete();
    }

    @Override
    public void checkDelete() throws IncorrectOperationException {
        psiClass.checkDelete();
    }

    @Override
    public void deleteChildRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {
        psiClass.deleteChildRange(psiElement, psiElement1);
    }

    @Override
    public PsiElement replace(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return psiClass.replace(psiElement);
    }

    @Override
    public boolean isValid() {
        return psiClass.isValid();
    }

    @Override
    public boolean isWritable() {
        return psiClass.isWritable();
    }

    @Nullable
    @Override
    public PsiReference getReference() {
        return psiClass.getReference();
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return psiClass.getReferences();
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(Key<T> key) {
        return psiClass.getCopyableUserData(key);
    }

    @Override
    public <T> void putCopyableUserData(Key<T> key, @Nullable T t) {
        psiClass.putCopyableUserData(key, t);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor psiScopeProcessor, @NotNull ResolveState resolveState, @Nullable PsiElement psiElement, @NotNull PsiElement psiElement1) {
        return psiClass.processDeclarations(psiScopeProcessor, resolveState, psiElement, psiElement1);
    }

    @Nullable
    @Override
    public PsiElement getContext() {
        return psiClass.getContext();
    }

    @Override
    public boolean isPhysical() {
        return psiClass.isPhysical();
    }

    @NotNull
    @Override
    public GlobalSearchScope getResolveScope() {
        return psiClass.getResolveScope();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return psiClass.getUseScope();
    }

    @Override
    public ASTNode getNode() {
        return psiClass.getNode();
    }

    @Override
    public boolean isEquivalentTo(PsiElement psiElement) {
        return psiClass.isEquivalentTo(psiElement);
    }

    @Override
    public Icon getIcon(int i) {
        return psiClass.getIcon(i);
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return psiClass.getUserData(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {
        psiClass.putUserData(key, t);
    }
}
