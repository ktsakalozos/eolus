# Building clouds for the MaDgIK lab #

"Management of Data Information, & Knowledge Group" (MaDgIK
http://www.madgik.di.uoa.gr/ ) at the University of Athens focuses on
several research areas, such as Database and Information Systems,
Distributed Systems, Query Optimization, and Digital Libraries. During
the past few years projects within this group started offering and
sharing hardware resources through a virtualized infrastructure. We
gradually got into building our own IaaS-cloud using open source
software, namely Xen, Debian and OpenNebula, but that was not enough,
we needed custom solutions to suit our needs. At that point we called
upon Eolus, the god of the winds, to blow and shape the clouds.

Eolus is our open source attempt to join the forces of an IaaS Cloud (currently OpenNebula) and
Java Enterprise Edition. It is far from achieving end product quality,
yet it provides functionality that serves our purposes. In short,
OpenNebula is used as a management tool for virtual resources that we
exploit in building higher level custom services available through a
JEE application container. An advance VM scheduler called Nefeli and a
web based administration console are only a couple of such high level
components we offer to users. Our success stories include
undergraduate theses, researchers and European funded projects (e.g.
http://www.d4science.eu/) experimenting and exploiting cloud
resources. Our efforts are released under the
EUPL licence.