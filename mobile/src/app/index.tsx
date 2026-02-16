import { useState } from "react";
import NovoCheckIn from "./sessao_estudo";
import Publicacao from "./publicacao";

export default function Index() {
  const [publicacao, setPublicacao] = useState(false);
  const [dadosCheckIn, setDadosCheckIn] = useState<any>(null);

  function handleCriarCheckIn(dados: any) {
    setDadosCheckIn(dados);
    setPublicacao(true);
  }

  function handleEditar() {
    setPublicacao(false);
  }

  return publicacao ? (
    <Publicacao
      dados={dadosCheckIn}
      onVoltar={() => setPublicacao(false)}
      onEditar={handleEditar}
    />
  ) : (
    <NovoCheckIn sessao={dadosCheckIn} onCriar={handleCriarCheckIn} />
  );

}
