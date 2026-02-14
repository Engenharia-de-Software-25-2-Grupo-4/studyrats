import { useState } from "react";
import CriarGrupo from "./criar_grupo";
import GrupoCriado from "./grupo_criado";

export default function Index() {
  const [grupoCriado, setGrupoCriado] = useState(false);
  const [dadosGrupo, setDadosGrupo] = useState<any>(null);

  function handleCriarGrupo(dados: any) {
    setDadosGrupo(dados);
    setGrupoCriado(true);
  }

  function handleEditar() {
    setGrupoCriado(false);
  }

  return grupoCriado ? (
    <GrupoCriado 
      dados={dadosGrupo}
      onVoltar={() => setGrupoCriado(false)}
      onEditar={handleEditar}
    />
  ) : (
    <CriarGrupo grupo={dadosGrupo} onCriar={handleCriarGrupo} />
  );

}
