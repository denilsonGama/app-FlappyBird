package br.com.gamadeveloper.games;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.Random;


public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;//Classe para criação de texturas
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    private Random numeroRandomico;
    private BitmapFont fonte;//Desenhar um texto
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    //private ShapeRenderer shape;//Classe para criação das colisões. Semelhante ao Batch



    //Atibutos de configuração
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo=0;// 0=jogo não iniciou 1=jogo iniciado 2=Game Over
    private int pontuacao=0;

    private float variacao = 0;
    private float velocidadeQueda=0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHOrizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto;//Padrão é false

    //Camara
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGTH = 1024;



	@Override
	public void create () {
		//Gdx.app.log("Create","Jogo Inicializado");
        batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaroCirculo = new Circle();
        /*retanguloCanoBaixo = new Rectangle();
        retanguloCanoTopo = new Rectangle();
        shape = new ShapeRenderer();*/
        fonte = new BitmapFont();
        fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        mensagem.getData().setScale(3);

        //Textura com varias imagens
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        //Configurando a camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGTH/2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGTH, camera);


        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGTH;
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoCanoHOrizontal = larguraDispositivo;
        espacoEntreCanos = 300;//Configurado para o Sony. Original =300

	}

	@Override
	public void render () {
	    camera.update();

	    //Limpar frAmaes anteriores OTIMIZA MEMORIA
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10; //A variacao recebe ela mesma a divisão dela pelo ultimo movimento
        if (variacao > 2) variacao = 0;//Serve para alterar entre as 3 imagens comecando do 0

        if (estadoJogo == 0) {//Não iniciado

            if (Gdx.input.justTouched() ) {
                estadoJogo = 1;
            }

        } else {//Jogo iniciado = 1

            velocidadeQueda++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical -= velocidadeQueda;

            if (estadoJogo == 1){

                posicaoMovimentoCanoHOrizontal -= deltaTime * 200;

            if (Gdx.input.justTouched()) {//Verifica se a tela foi tocada
                velocidadeQueda = -15;
                Gdx.app.log("Toque", "Houve toque na tela");
            }

            //Verifica se o cano saiu totalmente da tela
            if (posicaoMovimentoCanoHOrizontal < -canoTopo.getWidth()) {
                posicaoMovimentoCanoHOrizontal = larguraDispositivo - 100;
                alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;//400 é a metade da altura da tela. O numero sorteado deve ser entre 0 e 400
                //Se o nr sorteado for 100, ele tira 200 e o cano descerá na tela
                marcouPonto = false;//Quando o cano for recriado
            }
            //Verifica a pontuação
            if (posicaoMovimentoCanoHOrizontal < 120) {
                if (!marcouPonto) {
                    pontuacao += 10;
                    marcouPonto = true;
                }
            }

            } else{//Tela de GameOver ->2

                if(Gdx.input.justTouched()) {

                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHOrizontal = larguraDispositivo;
                }
            }
        }

        //Configurar dados de projeção da camera:
        batch.setProjectionMatrix(camera.combined);

        batch.begin();//Inciando as imagens


        //As img na ordem indicam o oque aparecerá primeiro
        //Posicionando a IMG de fundo para ocupar a tela full
        batch.draw(fundo,0,0, larguraDispositivo,alturaDispositivo);

        batch.draw(canoTopo, posicaoMovimentoCanoHOrizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(canoBaixo,posicaoMovimentoCanoHOrizontal,alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        batch.draw(passaros[(int)variacao],120,posicaoInicialVertical);//Eixo X e Y
        Gdx.app.log("Render", "Jogo Renderizando: " + variacao );
        fonte.draw(batch,String.valueOf(pontuacao),larguraDispositivo /2,alturaDispositivo-50); //batch é a estrutura onde desenhamos as texturas

        if(estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo / 2 -gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch,"Toque para Reiniciar",larguraDispositivo/ 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        batch.end();



        passaroCirculo.set(120 + passaros[0].getWidth() / 2,posicaoInicialVertical + passaros[0].getHeight() /2, passaros[0].getWidth() / 2);//Criação do passaro circle
        retanguloCanoBaixo = new Rectangle(posicaoMovimentoCanoHOrizontal,
                alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoBaixo.getWidth(),canoBaixo.getHeight() );

        retanguloCanoTopo = new Rectangle(posicaoMovimentoCanoHOrizontal,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoTopo.getWidth(), canoTopo.getHeight()
    );


        //Desenhar formas
        /*shape.begin( ShapeRenderer.ShapeType.Filled);//Forma de preencher as formas
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);//Posicionando o shape

        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);

        shape.setColor(com.badlogic.gdx.graphics.Color.RED);
        shape.end();*/


        //Teste de colisão
        //Verifica se houve uma insterscçaõ entre o passaro e as colunas
        //Teste de colisão
        if( Intersector.overlaps( passaroCirculo, retanguloCanoBaixo ) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
                || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ){
            estadoJogo = 2;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

    }
}
