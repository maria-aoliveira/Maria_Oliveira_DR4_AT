# Maria_Oliveira_DR4_AT
Segurança, criptografia, ads

Você deverá criar um aplicativo para guardar anotações com a possibilidade de inclusão de uma foto, mas pensando, desde o início, nos elementos de segurança. Também serão acrescentadas as informações de localização no início do texto da anotação. Como o aplicativo deverá ser monetizado, será adotada uma estratégia baseada em anúncios, os quais serão removidos com a obtenção da versão Premium.

Analisando o contexto, será necessário cumprir os seguintes requisitos:

- Tela para login ou inclusão de usuário com autenticação no Firebase.
- Tela para listagem das anotações, apresentando data e título em cada linha.
- Tela para cadastro e visualização da anotação, com data, título, foto e texto.
- A tela de login só deve ser apresentada se o usuário ainda não estiver logado.
- A tela de listagem deve mostrar os dados do usuário logado e permitir o logout.
- As informações de localização serão acrescentadas automaticamente no início do texto, apenas na inclusão.
- As permissões para acesso ao GPS serão solicitadas em tempo de execução.
- Acesso à câmera com Intent do sistema.
- Serão gerados dois arquivos internos para cada anotação: um para o texto e outro para a foto.
- O nome dos arquivos será no formato TÍTULO(DATA), com as extensões txt e fig.
- O conteúdo dos arquivos será criptografado.
- O Banner será exibido na tela de listagem, junto a um botão para desbloqueio da versão Premium.
- O botão e o Banner ficarão invisíveis com a conclusão da compra do produto gerenciado que dá direito à versão Premium, após reiniciar o app.
