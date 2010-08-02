package es.leafsoft.cineol;

public interface MovieHeaderViewDelegate {

	public void movieHeaderViewDidTouchMove(int offset);	
	public void movieHeaderViewDidTouchToLeftToRight();
	public void movieHeaderViewDidTouchToRightToLeft();
	public void movieHeaderViewDidTouchOnPosterThumbView();	
}
