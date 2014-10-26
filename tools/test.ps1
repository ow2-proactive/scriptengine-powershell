 param (
    [string]$server = "http://defaultserver",
    [string]$username = $(throw "-username is required."),
    [string]$password = $( Read-Host "Input password, please" )
 )