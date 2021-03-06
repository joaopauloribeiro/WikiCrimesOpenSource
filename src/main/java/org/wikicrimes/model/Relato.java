/**
 
    WikiCrimes (http://www.wikicrimes.org) is a project/software that allows posting and accessing criminal occurrences in a digital map.
    The philosophy that drives Wikicrimes is the same as Wikipedia: mass collaboration produces valuable knowledge.
    That is to say, if everybody participates, the criminal mapping will be made collaboratively and everybody
    will leverage crime information digitalized in the map. That is the reason for the slogan "Share crime information. Keep safe!". 
    Wikicrimes is not a project developed by any security institution. 
    In fact it is a project from the citizen to the citizen. 
     
    
    Copyright (C) 2008  Wikinova Solutions (http://www.wikinova.com.br)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.wikicrimes.model;

import java.util.Date;
import java.util.Set;

public class Relato extends GeoEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1466822619156909914L;

	/**
	 * 
	 */
	

	private Long idRelato;
	
	private String descricao;
	
	private String chave;
	
	private Boolean manha;
	
	private Boolean tarde;
	
	private Boolean noite;
	
	private Boolean madrugada;
	
	private UsuarioRedeSocial usuarioRedeSocial;
	
	private Usuario usuario;
	
	private UsuarioCelular usuarioCelular;
	
	private Double latitude;
	
	private Double longitude;
	
	private String ip;
	
	private String tipoRelato;
	
	private String subTipoRelato;

	private String endereco;
	
	private String cidade;
	
	private String estado;
	
	private String pais;
	
	private String cep;
	
    private Date dataHoraRegistro;	
	
    private Set<ConfirmacaoRelato> confirmacoes;    
    
    private String outraRazao;
    
    private Long qtdConfPositivas;
    
    private Long qtdConfNegativas;
    
    private Set<RelatoRazao> razoes;
    
    private String idicacaoValida;
    
    private TipoViolenciaEscolaRelato tipoViolenciaEscolaRelato;
    
    private TipoAgressorRelato tipoAgressorRelato;
    
    private TipoReportRelato tipoReportRelato;
    
    private TipoBemRoubadoRelato tipoBemRoubadoRelato;
    
    private TipoConsequenciaRelato tipoConsequenciaRelato;
    
    private TipoLocalizacaoRelato tipoLocalizacaoRelato;
    
    public Long getQtdConfPositivas() {
		return qtdConfPositivas;
	}

	public void setQtdConfPositivas(Long qtdConfPositivas) {
		this.qtdConfPositivas = qtdConfPositivas;
	}

	public Long getQtdConfNegativas() {
		return qtdConfNegativas;
	}

	public void setQtdConfNegativas(Long qtdConfNegativas) {
		this.qtdConfNegativas = qtdConfNegativas;
	}
	
	public String getOutraRazao() {
		return outraRazao;
	}

	public void setOutraRazao(String outraRazao) {
		this.outraRazao = outraRazao;
	}

	public String getTipoRelato() {
		return tipoRelato;
	}

	public void setTipoRelato(String tipoRelato) {
		this.tipoRelato = tipoRelato;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getIdRelato() {
		return idRelato;
	}

	public void setIdRelato(Long idRelato) {
		this.idRelato = idRelato;
	}
	
	@Override
	public Long getId() {
		return idRelato;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public Boolean getManha() {
		return manha;
	}

	public void setManha(Boolean manha) {
		this.manha = manha;
	}

	public Boolean getTarde() {
		return tarde;
	}

	public void setTarde(Boolean tarde) {
		this.tarde = tarde;
	}

	public Boolean getNoite() {
		return noite;
	}

	public void setNoite(Boolean noite) {
		this.noite = noite;
	}

	public Boolean getMadrugada() {
		return madrugada;
	}

	public void setMadrugada(Boolean madrugada) {
		this.madrugada = madrugada;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getSubTipoRelato() {
		return subTipoRelato;
	}

	public void setSubTipoRelato(String subTipoRelato) {
		this.subTipoRelato = subTipoRelato;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Date getDataHoraRegistro() {
		return dataHoraRegistro;
	}

	public void setDataHoraRegistro(Date dataHoraRegistro) {
		this.dataHoraRegistro = dataHoraRegistro;
	}

	public UsuarioRedeSocial getUsuarioRedeSocial() {
		return usuarioRedeSocial;
	}

	public void setUsuarioRedeSocial(UsuarioRedeSocial usuarioRedeSocial) {
		this.usuarioRedeSocial = usuarioRedeSocial;
	}

	public Set<ConfirmacaoRelato> getConfirmacoes() {
		return confirmacoes;
	}

	public void setConfirmacoes(Set<ConfirmacaoRelato> confirmacoes) {
		this.confirmacoes = confirmacoes;
	}

	public Set<RelatoRazao> getRazoes() {
		return razoes;
	}

	public void setRazoes(Set<RelatoRazao> razoes) {
		this.razoes = razoes;
	}

	public String getIdicacaoValida() {
		return idicacaoValida;
	}

	public void setIdicacaoValida(String idicacaoValida) {
		this.idicacaoValida = idicacaoValida;
	}

	public UsuarioCelular getUsuarioCelular() {
		return usuarioCelular;
	}

	public void setUsuarioCelular(UsuarioCelular usuarioCelular) {
		this.usuarioCelular = usuarioCelular;
	}

	public TipoViolenciaEscolaRelato getTipoViolenciaEscolaRelato() {
		return tipoViolenciaEscolaRelato;
	}

	public void setTipoViolenciaEscolaRelato(
			TipoViolenciaEscolaRelato tipoViolenciaEscolaRelato) {
		this.tipoViolenciaEscolaRelato = tipoViolenciaEscolaRelato;
	}
	
	public TipoAgressorRelato getTipoAgressorRelato() {
		return tipoAgressorRelato;
	}

	public void setTipoAgressorRelato(TipoAgressorRelato tipoAgressorRelato) {
		this.tipoAgressorRelato = tipoAgressorRelato;
	}

	public TipoReportRelato getTipoReportRelato() {
		return tipoReportRelato;
	}

	public void setTipoReportRelato(TipoReportRelato tipoReportRelato) {
		this.tipoReportRelato = tipoReportRelato;
	}

	public TipoConsequenciaRelato getTipoConsequenciaRelato() {
		return tipoConsequenciaRelato;
	}

	public void setTipoConsequenciaRelato(
			TipoConsequenciaRelato tipoConsequenciaRelato) {
		this.tipoConsequenciaRelato = tipoConsequenciaRelato;
	}

	public TipoLocalizacaoRelato getTipoLocalizacaoRelato() {
		return tipoLocalizacaoRelato;
	}

	public void setTipoLocalizacaoRelato(TipoLocalizacaoRelato tipoLocalizacaoRelato) {
		this.tipoLocalizacaoRelato = tipoLocalizacaoRelato;
	}

	public TipoBemRoubadoRelato getTipoBemRoubadoRelato() {
		return tipoBemRoubadoRelato;
	}

	public void setTipoBemRoubadoRelato(TipoBemRoubadoRelato tipoBemRoubadoRelato) {
		this.tipoBemRoubadoRelato = tipoBemRoubadoRelato;
	}	
	
}
