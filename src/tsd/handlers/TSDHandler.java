package tsd.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPComputableFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalFunction;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.concordia.tsd.util.IndexReferenceSearcher;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TSDHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public TSDHandler() {
		System.out.println("TSDHandler.TSDHandler()");
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) currentSelection;
			// Object[] selectedProjects = (Object[]) ss.toArray();
		}
		System.out.println("event " + event);
		System.out.println("TSDHandler.execute()");

		ICProject[] allProjects;
		try {
			allProjects = CoreModel.getDefault().getCModel().getCProjects();

			for (ICProject icProject : allProjects) {
				System.out.println("icProject " + icProject.getElementName());

				ICElement[] childrens = icProject.getChildren();

				for (ICElement icElement : childrens) {
					if (icElement instanceof IFolder) {
						System.out.println("Folder name " + icElement.getElementName());
					}
				}

				ISourceRoot[] allSourceRoots = icProject.getAllSourceRoots();
				System.out.println("allSourceRoots " + allSourceRoots.length);
				for (ISourceRoot iSourceRoot : allSourceRoots) {

					if (!iSourceRoot.getElementName().equals("subfolder")) {
						continue;
					}

					ITranslationUnit[] itus = iSourceRoot.getTranslationUnits();
					System.out.println("all ITUS " + itus.length);

					for (ITranslationUnit iTranslationUnit : itus) {
						if (iTranslationUnit.isCXXLanguage() && iTranslationUnit.isSourceUnit()) {
							System.out.println("file name " + iTranslationUnit.getElementName());
							System.out.println("INside isCXXLanguage and isSourceUnit");
							IASTTranslationUnit cppASTTu = iTranslationUnit.getAST();
							// IASTNodeSelector nodeSelector =
							// cppASTTu.getNodeSelector("HasSwitch");
							
							Set<IASTNode> parentFunctionCallNode = new HashSet<IASTNode>();

							cppASTTu.accept(new ASTVisitor(true) {
								{
									//shouldVisitExpressions = true;
									shouldVisitStatements = true;
								}
								
								/*/@Override
								public int visit(IASTExpression expression) {
									System.out.println("visit(expression)");
									
									if (expression instanceof IASTFunctionCallExpression) {
										IASTFunctionCallExpression fcexpr = (IASTFunctionCallExpression) expression;

										String rawSignature = fcexpr.getRawSignature();
										System.out.println("rawSignature " + rawSignature);

										try {
											IndexReferenceSearcher.outputReferences(
													fcexpr.getFunctionNameExpression().getRawSignature(), icProject);
										} catch (CoreException | InterruptedException e) {
											e.printStackTrace();
										}

										if (rawSignature.contains("HasSwitch")) {
											System.out.println(iTranslationUnit.getElementName());

											IASTInitializerClause[] arguments = fcexpr.getArguments();
											for (IASTInitializerClause argument : arguments) {
												System.out.println("argument " + argument.getRawSignature());
											}
										}
									}
									return super.visit(expression);
								}*/

								@Override
								public int visit(IASTStatement statement) {
//									System.out.println("visit(statement) " + statement.getRawSignature());
									
									if(statement instanceof ICPPASTIfStatement) {
										final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;
										System.out.println("cppIFStatement " + cppIFStatement.getRawSignature());
										
										IASTExpression conditionalExpression = cppIFStatement.getConditionExpression();
										System.out.println("condition " + conditionalExpression.getRawSignature());
										
										if(conditionalExpression instanceof ICPPASTFunctionCallExpression) {
											for (IASTNode iastNode : parentFunctionCallNode) {
												if(iastNode.contains(cppIFStatement.getOriginalNode())) {
													System.out.println("astNode is contained within the another IF statement");
												}
											}
											
											parentFunctionCallNode.add(cppIFStatement.getOriginalNode());
										}
									}
									
										
										/*
										final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;

										IASTExpression conditionalExpr = cppIFStatement.getConditionExpression();
										System.out.println("conditionExpr " + conditionalExpr.getRawSignature());

										if (conditionalExpr instanceof IASTFunctionCallExpression) {
											IASTFunctionCallExpression fcexpr = (IASTFunctionCallExpression) conditionalExpr;

											String rawSignature = fcexpr.getRawSignature();
											System.out.println("rawSignature " + rawSignature);
											
											IASTFileLocation fileLocation = cppIFStatement.getFileLocation();
											System.out.println("starting line number " + fileLocation.getStartingLineNumber() + ", endingLineNumber "
													+ fileLocation.getEndingLineNumber());
										}
									}
																		
									

									
									/*if (statement instanceof ICPPASTIfStatement) {
										System.out.println("statement is an if statement");
										final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;

										IASTFileLocation fileLocation = cppIFStatement.getFileLocation();
										System.out.println("starting line number " + fileLocation.getStartingLineNumber()
												+ ", endingLineNumber " + fileLocation.getEndingLineNumber());

										IASTNode originalNode = cppIFStatement.getOriginalNode();
										System.out.println("originalNode " + originalNode.hashCode());

										IASTNode parentNode = cppIFStatement.getParent();
										System.out.println("parentNode " + parentNode.hashCode());

										IASTNode[] allChildrens = cppIFStatement.getChildren();

										IASTExpression conditionalExpr = cppIFStatement.getConditionExpression();
										System.out.println("conditionExpr " + conditionalExpr.getRawSignature());
									}*/

									return super.visit(statement);
								}
							});
						}

					}

				}
				// ICElement[] allChildrens = icProject.getChildren();

				/*
				 * for (ICElement icElement : allChildrens) {
				 * System.out.println(icElement.getElementName());
				 * 
				 * icElement.accept(new ICElementVisitor() {
				 * 
				 * @Override public boolean visit(ICElement arg0) throws
				 * CoreException { System.out.println("argo " +
				 * arg0.getElementName()); System.out.println(
				 * "arg element type " + arg0.getElementType());
				 * System.out.println("arg0 element path " + arg0.getPath());
				 * 
				 * return true; } }); }
				 */

				/*
				 * IIndex index =
				 * CCorePlugin.getIndexManager().getIndex(icProject);
				 * 
				 * // IIndexFileSet iFileSet = index.createFileSet();
				 * icProject.accept(new ICElementVisitor() {
				 * 
				 * @Override public boolean visit(ICElement arg0) throws
				 * CoreException { return false; } });
				 * 
				 * // index.acquireReadLock();
				 * 
				 * IIndexBinding[] bindings =
				 * index.findBindings("attach".toCharArray(), IndexFilter.ALL,
				 * new NullProgressMonitor()); System.out.println(
				 * "bindings.length " + bindings.length);
				 * 
				 * for (IIndexBinding b : bindings) { if (b instanceof
				 * IFunction) { IIndexName[] names = index.findReferences(b);
				 * 
				 * System.out.println("names " + names.length);
				 * 
				 * for (IIndexName n : names) { IASTFileLocation fileLoc =
				 * n.getFileLocation(); System.out.println(fileLoc.getFileName()
				 * + " at offset " + fileLoc.getNodeOffset() + ", " +
				 * fileLoc.getStartingLineNumber()); } } }
				 */
			}
		} catch (CModelException e) {
			e.printStackTrace();
		} // catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		catch (CoreException e) {
			e.printStackTrace();
		} finally {

		}

		return null;
	}

	private void outputReferences(IIndex index, IBinding b) throws CoreException {
		System.out.println("SampleHandler.outputReferences()");

		IIndexName[] names = index.findReferences(b);

		System.out.println("names " + names.length);

		for (IIndexName n : names) {

			outputReference(index, n);
		}
	}

	private void outputReference(IIndex index, IIndexName n) throws CoreException {
		IASTFileLocation fileLoc = n.getFileLocation();
		System.out.println(fileLoc.getFileName() + " at offset " + fileLoc.getNodeOffset() + ", "
				+ fileLoc.getStartingLineNumber());

	}

}
